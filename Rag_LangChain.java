package com.ai.aitest.mylangchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.generativeaiinference.GenerativeAiInferenceClient;
import com.oracle.bmc.generativeaiinference.responses.ChatResponse;
import com.oracle.bmc.retrier.RetryConfiguration;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
//import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import com.ai.aitest.MyApp;
import com.ai.aitest.util.*;

public class Rag_LangChain {

	//final String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaaaav5u4xjffhmosvtmqdopcrqqgfb5qzti6jaornpbagsyhyiuclrca";
	GenerativeAiInferenceClient generativeAiInferenceClient;
	//String filesPath = "c:/ppp";
	// private String filePath = "C:\\New_Training\\PLSQL\\PLSQL_All_SG";

	public EmbeddingModel embeddingModel;
	public  ChatModel chatModel = null;
	private DocumentSplitter documentSplitter;

	private PromptTemplate template;
	

	private String queryStatement = "What is cursor";
	
	public Embedding queryEmbedding;
	public List<EmbeddingMatch<TextSegment>> matches;
	public String context;
	
	public List<Document> documents = new ArrayList<Document>();
	public List<TextSegment> textSegments;
	public List<Embedding> embeddings;
	EmbeddingStore embeddingStore;
	
	public void setPromptTempalte(String promptTemplate) {
		this.template = PromptTemplate
				.from(promptTemplate); 
	}
	
	public Rag_LangChain() {

		createAllMiniLmL6V2EmbeddingModel();
		embeddingStore = new InMemoryEmbeddingStore<TextSegment>();
		//createInMemoryEmbeddingStoreData();
		
	
		this.generativeAiInferenceClient = createAIClient();
		// this.chatModel = new ChatModel(generativeAiInferenceClient);
		// this.embModel = new MyEmbeddingModel(generativeAiInferenceClient);
		this.chatModel = ChatLlama.getChatLlamaInstance();
		this.chatModel.setModelName("meta.llama-3.3-70b-instruct");
		// Configure document splitter with desired chunk size and overlap
		this.documentSplitter = DocumentSplitters.recursive(800, // Maximum chunk size in tokens
				40, // Overlap between chunks
				null // Default separator
		);

		

		

		// create a LangChain4j PromptTemplate
		this.template = PromptTemplate
				.from("""
						You are a Plsql Troubleshooting Assistant. Answer the question in the context of PLSQL .
						Always ask if the user would like to know more about the topic. Do not add signature at the end of the answer.
						Use only the following pieces of context to answer the question at the end.

						Context: {{context}}

						Question: {{question}}

						Helpful Answer:
						""");

	}

	/**
	 * Creates a new instance of the Generative Ai Inference client.
	 *
	 * This method reads configuration settings from the specified config location
	 * and profile, authenticates using the provided authentication details, and
	 * establishes a connection to the Generative AI Inference service at the
	 * specified endpoint.
	 *
	 * @return a fully configured and authenticated Generative Ai Inference client
	 * @throws RuntimeException if there is an error reading the configuration file
	 *                          or authenticating
	 */
	
	

	public GenerativeAiInferenceClient createAIClient() {
		String ENDPOINT = "https://inference.generativeai.uk-london-1.oci.oraclecloud.com";
		String REGION = "uk-london-1";
		String CONFIG_LOCATION = "~/.oci/config";
		// TODO: Please update config profile name and use the compartmentId that has
		// policies grant permissions for using Generative AI Service
		String CONFIG_PROFILE = "LONDON2";

		ConfigFileReader.ConfigFile configFile;
		AuthenticationDetailsProvider provider;

		ClientConfiguration clientConfiguration;
		GenerativeAiInferenceClient generativeAiInferenceClient;

		// read configuration details from the config file and create a
		// AuthenticationDetailsProvider
		try {
			configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
			provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		clientConfiguration = ClientConfiguration.builder().readTimeoutMillis(240000)
				.retryConfiguration(RetryConfiguration.NO_RETRY_CONFIGURATION).build();

		generativeAiInferenceClient = GenerativeAiInferenceClient.builder().configuration(clientConfiguration)
				.endpoint(ENDPOINT).region(REGION).build(provider);

		return generativeAiInferenceClient;
	}

	/**
	 * Loads PDF files from the specified file path, parses their contents using
	 * ApachePdfBoxDocumentParser, and splits them into individual TextSegments.
	 *
	 * @param filePath the path to the directory containing the PDF files
	 * @return a list of TextSegments representing the parsed and split PDF text
	 *         content
	 */
	
	public void loadDocuemnts(List<File> listFile) {
		documents.removeAll(documents);
		
		DocumentParser parser = new ApacheTikaDocumentParser();
     	if (listFile != null) {
            for (File file : listFile) {
            	String fileName= file.getAbsoluteFile().toString();
            	System.out.println("file.getAbsoluteFile().toString(): "+fileName);	
            	//Document document = FileSystemDocumentLoader.loadDocument(file.getAbsoluteFile().toString(), parser);
            //	System.out.println(file.getName());
            	Document document;
				try {
					document = parser.parse(new FileInputStream(fileName));
					documents.add(document);
					
					chunkFiles();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   	
            }
     	}
	}
	
	public void loadPDFFiles(String filesPath){
	/*	try {
			// Load all *.pdf documents from the given directory
			System.out.println("Direcotry in loadPDFFILES is " + filesPath);
			PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
			documents = FileSystemDocumentLoader.loadDocuments(filesPath, pathMatcher, new ApachePdfBoxDocumentParser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("documents size " + documents.size());
		
		textSegments=chunkPDFFiles();
		*/
	}
	
	public void chunkFiles() {
		
		 textSegments = documents.stream().flatMap(d -> documentSplitter.split(d).stream()).toList();
		int i = 0;
		for (TextSegment ts : textSegments) {
			System.out.println("----------------begin " + i);
			System.out.println(ts.text());
			System.out.println("the length of Text is: " + ts.text().length());
			System.out.println("----------------end " + i);
			i++;
		}
		
		createEmbeddings(textSegments);
		//return textSegments;
	}
	
	public TextSegment getChunk(int chunkno) {
		
		return textSegments.get(chunkno);
		
	}
	
	public List<TextSegment> getChunksAll() {
		
		return textSegments;
		
	}
	
	
	public void createEmbeddings(List<TextSegment> segments) {
		// public void createEmbeddings(List<TextSegment> segments) {
		Response<List<Embedding>> response = embeddingModel.embedAll(segments);
		embeddings = response.content();
		int i = 0;
		for (Embedding eb : embeddings) {
			System.out.println("---------------- embedding begin" + i);
			System.out.println(eb.dimension() + " is " + eb.toString());
			System.out.println("----------------embedding end");
			i++;
		}
		
	}
	
	public String getVector(String myPrompt) {
		
		Response<Embedding> response = embeddingModel.embed(myPrompt);
		return response.toString();
	}
	
	
	
	public Embedding getEmbedding(int embeddingNo) {
		
		return embeddings.get(embeddingNo);
		
	}
	
	public List<Embedding> getEmbeddingsAll() {
		
		return embeddings;
		
	}
	
	
	public void createAllMiniLmL6V2EmbeddingModel() {

		embeddingModel = new AllMiniLmL6V2EmbeddingModel();

	}
	
	public void createInMemoryEmbeddingStoreData() {
		if (embeddingStore != null) {
			embeddingStore.removeAll();
		}
		//List<TextSegment> segments = chunkPDFFiles(filePath);
		//List<Embedding> list_eb = createEmbeddings(segments);
		storeEmbeddings(embeddings, textSegments);

	}

	public void storeEmbeddings(List<Embedding> embeddings, List<TextSegment> segments) {
		System.out.println("you are trying to store the embedding and segments");
		embeddingStore.addAll(embeddings, segments);
		System.out.println("after embeddingStore.addAll(embeddings, segments);");
		System.out.println ("print embedding:---------"+embeddings.size());
		System.out.println ("print segments :---------"+segments.size());
		
	}
	

	public void vectorSearch(String query) {
		
		Response<Embedding> response = embeddingModel.embed(query);
		queryEmbedding = response.content();
		int maxResults = 2;
		double minScore = 0.6;

		EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
				.queryEmbedding(queryEmbedding).maxResults(maxResults).build();
		
		matches = embeddingStore.search(embeddingSearchRequest).matches();

		context = matches.stream().map(match -> match.embedded().text()).collect(Collectors.joining("\n\n"));
		
		System.out.println("---------------- start printing the context ");
	    System.out.println("context is : "+ context);
	    System.out.println("---------------- ending printing the context ");
	    
	   int  i=0;
	    for(EmbeddingMatch<TextSegment> em: matches) {
			System.out.println("---------------- embedding Match " + i);
			//System.out.println("embedded " + " is: " + em.embedded());
			System.out.println("em.score() "+ em.score());
			 System.out.println(em.embedded().text());
			//System.out.println("embedding  " + " is: " + em.embedding());
			System.out.println("----------------embedding match end");
			i++;
	    }
	}
	
	public String getRagPrompt(String queryStatement) {
        Map<String, Object> variables = Map.of(
                "question", queryStatement,
                "context", context
        );

        Prompt prompt = template.apply(variables);
        
		System.out.println("--------printing the Prompt sent to the LLM ------");
		 System.out.println(prompt.text());
		 System.out.println("---------- ending of printing the prompt---------");
        
        chatModel.setPrompt(prompt.text());
        
        return prompt.text();
		
	}
	
	
	public String getQueryResult(String queryStatement) throws Exception{
		
        Map<String, Object> variables = Map.of(
                "question", queryStatement,
                "context", context
        );

        Prompt prompt = template.apply(variables);
        
		System.out.println("--------printing the Prompt sent to the LLM ------");
		 System.out.println(prompt.text());
		 System.out.println("---------- ending of printing the prompt---------");
        
        chatModel.setPrompt(prompt.text());
       
        String answer = chatModel.chat();
        return answer;
		
	}
	


	public static void main(String[] args) throws Exception {
		Rag_LangChain rlc = new Rag_LangChain();
		rlc.createAllMiniLmL6V2EmbeddingModel();
		rlc.createInMemoryEmbeddingStoreData();

		Response<Embedding> response = rlc.embeddingModel.embed(rlc.queryStatement);
		// Embedding queryEmbedding = embeddingModel.embed("What is your favourite
		// sport?").content();
		rlc.queryEmbedding = response.content();

		int maxResults = 2;
		double minScore = 0.7;

		EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
				.queryEmbedding(rlc.queryEmbedding).maxResults(maxResults).build();
		List<EmbeddingMatch<TextSegment>> matches = rlc.embeddingStore.search(embeddingSearchRequest).matches();

		String context = matches.stream().map(match -> match.embedded().text()).collect(Collectors.joining("\n\n"));
		
		System.out.println("---------------- start printing the context ");
	    System.out.println("context is : "+ context);
	    System.out.println("---------------- ending printing the context ");
	   int  i=0;
	    for(EmbeddingMatch<TextSegment> em: matches) {
			System.out.println("---------------- embedding Match " + i);
			//System.out.println("embedded " + " is: " + em.embedded());
			System.out.println("em.score() "+ em.score());
			 System.out.println(em.embedded().text());
			//System.out.println("embedding  " + " is: " + em.embedding());
			System.out.println("----------------embedding match end");
			i++;

	    }
	    
        // add the question and the retrieved context to the prompt template

        Map<String, Object> variables = Map.of(
                "question", rlc.queryStatement,
                "context", context
        );

        Prompt prompt = rlc.template.apply(variables);
        
		System.out.println("--------printing the Prompt sent to the LLM ------");
		 System.out.println(prompt.text());
		 System.out.println("---------- ending of printing the prompt---------");
        
        rlc.chatModel.setPrompt(prompt.text());
        String answer = rlc.chatModel.chat();
		//System.out.println("--------printing answer from the LLM ------");
		// System.out.println(answer);

        /*
    	public List<TextSegment> chunkPDFFiles(String filePath) {

    		List<Document> documents = null;
    		try {
    			// Load all *.pdf documents from the given directory
    			PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
    			documents = FileSystemDocumentLoader.loadDocuments(filePath, pathMatcher, new ApachePdfBoxDocumentParser());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		System.out.println("documents size " + documents.size());

    		for (Document document : documents) {
    			// documents.forEach(e -> System.out.println(e.metadata().toString()));

    			// System.out.println(document.toTextSegment());
    			// System.out.println(e.toTextSegment());

    		}

    		// Split documents into TextSegments and add them to a List

    		List<TextSegment> list_ts = documents.stream().flatMap(d -> documentSplitter.split(d).stream()).toList();
    		int i = 0;
    		for (TextSegment ts : list_ts) {
    			System.out.println("----------------begin" + i);
    			System.out.println(ts.text());
    			System.out.println("the length of Text is: " + ts.text().length());
    			System.out.println("----------------end");
    			i++;
    		}
    		return list_ts;
    	}
    */
    	
	}

}
