package com.ai.aitest.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.scene.layout.*;
import com.ai.aitest.*;
import com.ai.aitest.util.ObjectStorageUtil;

public class MyObjectStorage_Controller {

	ObjectStorageUtil objStore;
	
    @FXML
    private ListView<String> lv_directory;
    
    @FXML
    private ListView<String> lv_filenames;
    
    @FXML
    private Button bt_newbucket;
    @FXML
    private Button bt_deletebucket;
    @FXML
    private Button bt_uploadfile;
    @FXML
    private Button bt_deletefile;
    @FXML
    private Button bt_downloadfile;
    @FXML
    private Button bt_downloadbucket;
    @FXML
    private Button bt_url;
    
    @FXML
    private TextField tf_bucketname;
    
  
      String currentBucketName;
      String currentFileName;
      List<String> bucketFileNames;
      List<File> uploadFileNames;
      List<File> selectedFileNames;
    
    public void initialize() {
    	
    	lv_directory.getItems().clear();
    	lv_filenames.getItems().clear();
    	lv_filenames.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
    	Alert a = new Alert(AlertType.NONE);
    	
    	objStore=ObjectStorageUtil.getInstance();
		List<String> listBuckets = objStore.getAllBuckets();
		
		if (listBuckets != null) {
			for (String bucket : listBuckets) {
				// System.out.println(file.getName());
				lv_directory.getItems().add(bucket);
			}
		}
		
	////////////////	
	lv_directory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    		
    		public void changed(ObservableValue<? extends String> arg0,String arg1,String arg2){
    			
    			currentBucketName=lv_directory.getSelectionModel().getSelectedItem();
    			System.out.println(currentBucketName);
       			bucketFileNames=objStore.getBucketObjects(currentBucketName);
    			lv_filenames.getItems().setAll(bucketFileNames);
    		}
		});
    
    ///////////// 
	lv_filenames.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
   		public void changed(ObservableValue<? extends String> arg0,String arg1,String arg2){
			
   			currentFileName=lv_filenames.getSelectionModel().getSelectedItem();
			System.out.println(currentFileName);
			
		}
	});
	
	
    ///////////////////////////
    bt_uploadfile.setOnAction(e -> {
    	
      	FileChooser fileChooser = new FileChooser();
      	fileChooser.setInitialDirectory(new File("/home/opc/demo"));
      	fileChooser.setTitle("Open upload to Bucket Files");
      	
    	uploadFileNames =
                fileChooser.showOpenMultipleDialog(MyApp.currentStage);
       	if (uploadFileNames != null) {
              for (File file : uploadFileNames) {
              //	System.out.println(file.getName());
            	  //lv_filenames.getItems().add(file.to)
            	  try {
					objStore.putObject(currentBucketName,file);
					lv_filenames.getItems().add(file.getName());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
              }

      	}       	
    	            
    });
    
    ////////////////////
    
    bt_deletefile.setOnAction(e -> {
    	
      	 System.out.println("***** you are been selected");
      	 
    	 ObservableList<String> selectedItems = lv_filenames.getSelectionModel().getSelectedItems();   

    	 
    	 System.out.println("the size of list: "+selectedItems.size());


    	 int i=0;
         for(String fileName : selectedItems){
        	 System.out.println("-----------in the delete action");
        	 System.out.println("delete "+i+"  "+fileName);
        	 i++;

        	 objStore.deleteObject(currentBucketName, fileName);
        	
        	
         }
			bucketFileNames=objStore.getBucketObjects(currentBucketName);
			lv_filenames.getItems().setAll(bucketFileNames);
      
     });
    
   ///////////////
    bt_newbucket.setOnAction(e -> {
    	
    	objStore.createNewBucket(tf_bucketname.getText());
    	
		List<String> buckets = objStore.getAllBuckets();
		
		lv_directory.getItems().setAll(buckets);
		
    });

    //////////////
    
    bt_deletebucket.setOnAction(e -> {
    	
    	objStore.deleteBucket(currentBucketName);
		List<String> buckets = objStore.getAllBuckets();
		
		lv_directory.getItems().setAll(buckets);
    	
    });
    
    /////////////////////////////
    
    bt_downloadfile.setOnAction(e -> {
    	try {
			objStore.getObject(currentBucketName, currentFileName);
			 
		        a.setAlertType(AlertType.CONFIRMATION);

                // set content text
                a.setContentText("The File has been downloaded into /home/opc/demo/TEMP directory!!!");
                a.show();
			 
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    });
    
    ///////////////////////
    
    bt_downloadbucket.setOnAction(e -> {
    	
    	List<String> listFile= objStore.getBucketObjects(currentBucketName);
    	for (String file:listFile) {
    		
    		try {
				objStore.getObject(currentBucketName, file);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}  	
    	  a.setAlertType(AlertType.CONFIRMATION);
          // set content text
          a.setContentText("All Files in the  "+currentBucketName+" have been downloaded into /home/opc/demo/TEMP !!!");
          a.show();
    	
    });
    
    ////////////////////
    
bt_url.setOnAction(e -> {
	
	String url= "https://objectstorage.uk-london-1.oraclecloud.com/n/lrgpkhlte873/b/"
			+currentBucketName+"/o/"+currentFileName;
	
	  a.setAlertType(AlertType.INFORMATION);
      // set content text
      a.setContentText(url);
      
      
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("URL for the File: "+ currentFileName);
      alert.setHeaderText("File Name: "+ currentFileName);

      TextArea textArea = new TextArea();
      textArea.setText(url);
      textArea.setWrapText(true);
      textArea.setEditable(false);

      VBox dialogPaneContent = new VBox(textArea);
      alert.getDialogPane().setContent(dialogPaneContent);
      alert.showAndWait();
      
});

    }
}

