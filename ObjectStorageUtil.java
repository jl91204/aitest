package com.ai.aitest.util;

import com.ai.aitest.MyApp;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.generativeaiinference.GenerativeAiInferenceClient;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageAsync;
import com.oracle.bmc.objectstorage.ObjectStorageAsyncClient;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.DeleteBucketRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreateBucketResponse;
import com.oracle.bmc.objectstorage.responses.DeleteObjectResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.responses.AsyncHandler;
import com.oracle.bmc.retrier.RetryConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;





public class ObjectStorageUtil {
	
	ObjectStorage objectStorageClient;
	public String namespaceName;
	AuthenticationDetailsProvider provider;
	private static ObjectStorageUtil instance;
	
	private ObjectStorageUtil() {
		createObjectStorage();
		}

	public static synchronized ObjectStorageUtil getInstance() {
		if (instance == null) {
		instance = new ObjectStorageUtil();
		}
		return instance;
		}
	
	public  void  createObjectStorage(){
	       String configurationFilePath = "~/.oci/config";
	        String profile = "LONDON2";

	        try {
	        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath, profile);

	         provider =
	                new ConfigFileAuthenticationDetailsProvider(configFile);

	         objectStorageClient =
	                ObjectStorageClient.builder().region(Region.UK_LONDON_1).build(provider);
	        GetNamespaceResponse namespaceResponse =
	        		objectStorageClient.getNamespace(GetNamespaceRequest.builder().build());
	        namespaceName = namespaceResponse.getValue();
	        System.out.println("Using namespace: " + namespaceName);
	        }catch (Exception e) {}

	         
	      
	}

	
	
	public List<String> getAllBuckets(){
		
		   List<String> list_buckets= new ArrayList<>();
	       Builder listBucketsBuilder =
	                ListBucketsRequest.builder()
	                        .namespaceName(namespaceName)
	                        .compartmentId(provider.getTenantId());

	        String nextToken = null;
	        do {
	            listBucketsBuilder.page(nextToken);
	            ListBucketsResponse listBucketsResponse =
	            		objectStorageClient.listBuckets(listBucketsBuilder.build());
	            
	            for (BucketSummary bucket : listBucketsResponse.getItems()) {
	                System.out.println("Found bucket: " + bucket.getName());
	                list_buckets.add(bucket.getName());
	            }
	            nextToken = listBucketsResponse.getOpcNextPage();
	            
	        } while (nextToken != null);
	        
		for (String s: list_buckets) {
			System.out.println("--------------------");
			System.out.println("bucket in the list : " + s);
			
		}
		
		return list_buckets;
	}
	
	
	public List<String> getBucketObjects(String bucketName){
		
		List<String> list_objects= new ArrayList<>();
		
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
        		.bucketName(bucketName)
        		.namespaceName(namespaceName)
        		.build();
        
        ListObjectsResponse listObjectsResponse = objectStorageClient.listObjects(listObjectsRequest);
        
        
        List<ObjectSummary> list_objectSummary= listObjectsResponse.getListObjects().getObjects();
        
        for(ObjectSummary os:list_objectSummary) {
        	
        	System.out.println("Found Object: " + os.getName());
        	list_objects.add(os.getName());
        	
        }
		
		for (String s: list_objects) {
			System.out.println("--------------------");
			System.out.println("Objects in the list : " + s);
			
		}
        return list_objects;
	}
	
	
	public  void putObject(String bucketName,File body) throws Exception{

        Map<String, String> metadata = null;
        String contentType = null;
        String contentEncoding = null;
        String contentLanguage = null;
        String objectName=body.getName();
       // File body = new File(objectName);
      //  File body = new File("c:\\ppp\\image.png");

         // configure upload settings as desired
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(objectStorageClient, uploadConfiguration);

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(namespaceName)
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(contentLanguage)
                        .contentEncoding(contentEncoding)
                        .opcMeta(metadata)
                        .build();

        UploadRequest uploadDetails =
                UploadRequest.builder(body)
                .allowOverwrite(true).build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw
        // BmcException
        UploadResponse response = uploadManager.upload(uploadDetails);
        System.out.println(response);

        // fetch the object just uploaded
        GetObjectResponse getResponse =
        		objectStorageClient.getObject(
                        GetObjectRequest.builder()
                                .namespaceName(namespaceName)
                                .bucketName(bucketName)
                                .objectName(objectName)
                                .build());

        // use the response's function to print the fetched object's metadata
        System.out.println(getResponse.getOpcMeta());

        // stream contents should match the file uploaded
        try (final InputStream fileStream = getResponse.getInputStream()) {
            // use fileStream
        } // try-with-resources automatically closes fileStream
    }
	
	//////////////////////////////////////
	
	public  void deleteObject(String bucketName,String objectName) {
	    
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.namespaceName(namespaceName)
				.bucketName(bucketName)
				.objectName(objectName)
				//.ifMatch("EXAMPLE-ifMatch-Value")
				//.opcClientRequestId("ocid1.test.oc1..<unique_ID>EXAMPLE-opcClientRequestId-Value")
				//.versionId("ocid1.test.oc1..<unique_ID>EXAMPLE-versionId-Value")
				.build();

		        /* Send request to the Client */
		        DeleteObjectResponse response = objectStorageClient.deleteObject(deleteObjectRequest);
		    }

	/////////////////
	
	public  void createNewBucket(String bucketName) {

        CreateBucketResponse createBucketResponse =
                objectStorageClient.createBucket(
                        CreateBucketRequest.builder()
                                .namespaceName(namespaceName)
                                .createBucketDetails(
                                        CreateBucketDetails.builder()
                                                .name(bucketName)
                                                .compartmentId(MyApp.COMPARTMENT_ID)
                                                .publicAccessType(
                                                        CreateBucketDetails.PublicAccessType
                                                                .ObjectRead)
                                             //   .freeformTags(freeformTags)
                                              //  .definedTags(definedTags)
                                                .build())
                                .build());
        System.out.println("Created a bucket with tags:\n " + createBucketResponse.getBucket());
        System.out.println("=========================\n");

    }
	
	/////////////////////////////////
	
	public  void getObject (String bucketName,String objectName) throws Exception{

		String CONFIG_LOCATION = "~/.oci/config";
		// TODO: Please update config profile name and use the compartmentId that has
		// policies grant permissions for using Generative AI Service
		String CONFIG_PROFILE = "LONDON2";

		ConfigFileReader.ConfigFile configFile;
		AuthenticationDetailsProvider provider;


		// read configuration details from the config file and create a
		// AuthenticationDetailsProvider
		try {
			configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
			provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

        
        ObjectStorageAsync client =
                ObjectStorageAsyncClient.builder().region(Region.UK_LONDON_1).build(provider);
        ResponseHandler<GetNamespaceRequest, GetNamespaceResponse> namespaceHandler =
                new ResponseHandler<>();
        
      
        client.getNamespace(GetNamespaceRequest.builder().build(), namespaceHandler);
        GetNamespaceResponse namespaceResponse = namespaceHandler.waitForCompletion();

        String namespaceName = namespaceResponse.getValue();
        System.out.println("Using namespace: " + namespaceName);
        
        client.getNamespace(GetNamespaceRequest.builder().build(), namespaceHandler);
         System.out.println("Using namespace: " + namespaceName);
  
        ResponseHandler<GetObjectRequest, GetObjectResponse> objectHandler =
                new ResponseHandler<>();
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .objectName(objectName)
                        .build();
        client.getObject(getObjectRequest, objectHandler);
        GetObjectResponse getResponse = objectHandler.waitForCompletion();

        // stream contents should match the file uploaded
        try (final InputStream fileStream = getResponse.getInputStream()) {
            // use fileStream
        	File targetFile = new File("/home/opc/demo/TEMP/"+objectName);

        	try (OutputStream outputStream = new FileOutputStream(targetFile)) {
        		fileStream.transferTo(outputStream);
        	}
        	
        } // try-with-resources automatically closes fileStream

        //objectStorageClient.close();
    }
	
        
	
	public  void deleteBucket(String bucketName) {
		

        objectStorageClient.deleteBucket(
                DeleteBucketRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .build());
        System.out.println("Deleted bucket");
    }

	
	   public static void main(String[] args) throws Exception {

		   ObjectStorageUtil os = new ObjectStorageUtil();
		   os.createObjectStorage();
		   os.getAllBuckets();
		   os.getBucketObjects("java");
		   
		   /*
	        String configurationFilePath = "~/.oci/config";
	        String profile = "LONDON2";

	        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI
	        // config file
	        // "~/.oci/config", and a profile in that config with the name "DEFAULT". Make changes to
	        // the following
	        // line if needed and use ConfigFileReader.parse(configurationFilePath, profile);

			
	        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath, profile);

	        final AuthenticationDetailsProvider provider =
	                new ConfigFileAuthenticationDetailsProvider(configFile);

	        ObjectStorage client =
	                ObjectStorageClient.builder().region(Region.UK_LONDON_1).build(provider);

	        GetNamespaceResponse namespaceResponse =
	                client.getNamespace(GetNamespaceRequest.builder().build());
	        String namespaceName = namespaceResponse.getValue();
	        System.out.println("Using namespace: " + namespaceName);

	        Builder listBucketsBuilder =
	                ListBucketsRequest.builder()
	                        .namespaceName(namespaceName)
	                        .compartmentId(provider.getTenantId());

	        String nextToken = null;
	        do {
	            listBucketsBuilder.page(nextToken);
	            ListBucketsResponse listBucketsResponse =
	                    client.listBuckets(listBucketsBuilder.build());
	            for (BucketSummary bucket : listBucketsResponse.getItems()) {
	                System.out.println("Found bucket: " + bucket.getName());
	            }
	            nextToken = listBucketsResponse.getOpcNextPage();
	        } while (nextToken != null);

	        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
	        		.bucketName("plsql")
	        		.namespaceName(namespaceName)
	        		.build();
	        
	        ListObjectsResponse listObjectsResponse = client.listObjects(listObjectsRequest);
	        
	        
	        List<ObjectSummary> list_objects= listObjectsResponse.getListObjects().getObjects();
	        
	        for(ObjectSummary os:list_objects) {
	        	
	        	System.out.println("Found Object: " + os.getName());
	        	
	        }
	        
	        /*
	        // fetch the file from the object storage
	        String bucketName = "pdf";
	        String objectName = "*.pdf";
	        GetObjectResponse getResponse =
	                client.getObject(
	                        GetObjectRequest.builder()
	                                .namespaceName(namespaceName)
	                                .bucketName(bucketName)
	                                .objectName(objectName)
	                                .build());

	        // stream contents should match the file uploaded
	        try (final InputStream fileStream = getResponse.getInputStream()) {
	            // use fileStream
	        } // try-with-resources automatically closes fileStream

	        client.close();
	        	*/
	    }
	   private static class ResponseHandler<IN, OUT> implements AsyncHandler<IN, OUT> {
		    private OUT item;
		    private Throwable failed = null;
		    private CountDownLatch latch = new CountDownLatch(1);

		    private OUT waitForCompletion() throws Exception {
		        latch.await();
		        if (failed != null) {
		            if (failed instanceof Exception) {
		                throw (Exception) failed;
		            }
		            throw (Error) failed;
		        }
		        return item;
		    }

		    @Override
		    public void onSuccess(IN request, OUT response) {
		        item = response;
		        latch.countDown();
		    }

		    @Override
		    public void onError(IN request, Throwable error) {
		        failed = error;
		        latch.countDown();
		    }
		}
}
