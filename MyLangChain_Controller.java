package com.ai.aitest.controller;

import com.ai.aitest.MyApp;

import com.ai.aitest.mylangchain.Rag_LangChain;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MyLangChain_Controller {
	
	 
	
    @FXML
    private Button bt_files;
    @FXML
    private Button bt_textsegments;
    
    @FXML
    private Button bt_segment1;
    @FXML
    private Button bt_segment2;
    @FXML
    private Button bt_segment3;
    @FXML
    private Button bt_segmentall;
    
    @FXML
    private Button  bt_template;
    
    @FXML
    private Button bt_embedding1;
    @FXML
    private Button bt_embedding2;
    @FXML
    private Button bt_embedding3;
    @FXML
    private Button bt_embeddingall;

    @FXML
    private Button bt_storevector;
    
    @FXML
    private Button bt_vectorsearch;
    
    @FXML
    private Button bt_vectormatch;
    

    
    @FXML
    private Button bt_submit;
    
    @FXML
    private Button bt_clear;
    
    @FXML
    private TextArea ta_display;
    @FXML
    private TextArea ta_prompt;
    
    
    @FXML
    private ChoiceBox cb_filenames;
    @FXML
    private ComboBox<String>  ComboBox_prompt;
    
    List<File> listFile;
    
    Rag_LangChain rag_LangChain;
    
	String plsql_template="""
			You are a Plsql Troubleshooting Assistant. Answer the question in the context of PLSQL .
			Always ask if the user would like to know more about the topic. Do not add signature at the end of the answer.
			Use only the following pieces of context to answer the question at the end.

			Context: {{context}}

			Question: {{question}}

			Helpful Answer:
			""";
	String java_template="""
			You are a java Assistant. Answer the question in the context of java program language .
			Always ask if the user would like to know more about the topic. Do not add signature at the end of the answer.
			Use only the following pieces of context to answer the question at the end.

			Context: {{context}}

			Question: {{question}}

			Helpful Answer:
			""";
	
	String general_template="""
			You are a personal Assistant. Answer the question in the context .
			Always ask if the user would like to know more about the topic. Do not add signature at the end of the answer.
			Use only the following pieces of context to answer the question at the end.

			Context: {{context}}

			Question: {{question}}

			Helpful Answer:
			""";
	
    
    public MyLangChain_Controller() {      	
    }
        
    public void initialize(){
    	rag_LangChain=new Rag_LangChain();
        
      	FileChooser fileChooser = new FileChooser();
      	fileChooser.setInitialDirectory(new File("/home/opc/demo"));
      	fileChooser.setTitle("Open Resource Files");
      	
       	//DirectoryChooser directoryChooser = new DirectoryChooser();
       //	File initialDirectory=new File("c:\\ppp");
       //	directoryChooser.setInitialDirectory(initialDirectory);

        // set initial File
       // file_chooser.setInitialDirectory(new File("c:\\ppp"));
       // file_chooser.getExtensionFilters().addAll(
       //         new FileChooser.ExtensionFilter("All PDF", "*.pdf")
        //    );
        cb_filenames.setTooltip(new Tooltip("Selected file names"));
        
        
   	  // Stage currentStage=(Stage) bt_directory.getScene().getWindow();
       ///////////////////////
        bt_files.setOnAction(e -> {
        	ta_display.setText("");
        	cb_filenames.getItems().clear();
        	
            listFile =
                    fileChooser.showOpenMultipleDialog(MyApp.currentStage);
           	if (listFile != null) {
                  for (File file : listFile) {
                  //	System.out.println(file.getName());
                  	cb_filenames.getItems().add(file.getAbsoluteFile());
                  }
                  rag_LangChain.loadDocuemnts(listFile);
          	}       	
        	            
        	//rag_LangChain.loadPDFFiles(selectedDirectory.getPath());
        	
        	//System.out.println("selectedDirectory.getPath(): " + selectedDirectory.getPath());
        	
            //File directoryPath = new File(selectedDirectory.getAbsoluteFile());
          //  FilenameFilter filter = (dir, name) -> name.endsWith(".pdf");

            //String[] listFile= selectedDirectory.list(filter);
            
            
            

        	
        });
        /////////////////////
        bt_segment1.setOnAction(e -> {
        	String seperator ="\n\n------------------- segment 1  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getChunk(0).text());	   	
        });
///////////////////
        bt_segment2.setOnAction(e -> {
        	String seperator ="\n\n------------------- segment 2  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getChunk(1).text());	   	
        });
        /////////////////
        bt_segment3.setOnAction(e -> {
        	String seperator ="\n\n------------------- segment 3  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getChunk(2).text());	   	
        });
        ///////////////////
        bt_segmentall.setOnAction(e -> {
        	//String seperator ="\n\n------------------- Segment all and the number of Total Segments "+ rag_LangChain.textSegments.size() +" ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	//ta_display.appendText(seperator);
        	//ta_display.appendText(rag_LangChain.textSegments.toString());	   	
        	int i=0;
        	for (TextSegment ts:rag_LangChain.getChunksAll()) {
        		String seperator ="\n------------------- Segment "+ i+ " ------------\n\n";
        		ta_display.appendText(seperator);
        		ta_display.appendText(ts.text());
        		i++;
        	}      	
        });
        ///////////////////
        bt_embedding1.setOnAction(e -> {
        	
        	if(rag_LangChain.getEmbeddingsAll()==null) {rag_LangChain.createEmbeddings(rag_LangChain.textSegments);}
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	String seperator ="\n\n------------------- Embedding Vector 1 and Embedding Dimension is: "
        			+rag_LangChain.embeddingModel.dimension() + " ------------------------\n\n";
        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getEmbedding(0).toString());   	
        });
        
        /////////////
        
        bt_embedding2.setOnAction(e -> {
        	        	if(rag_LangChain.getEmbeddingsAll()==null) {rag_LangChain.createEmbeddings(rag_LangChain.textSegments);}
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	String seperator ="\n\n------------------- Embedding Vector 2 and Embedding Dimension is: "
        			+rag_LangChain.embeddingModel.dimension() + " ------------------------\n\n";

        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getEmbedding(1).toString());   	
        });
        
        ////////////////
        bt_embedding3.setOnAction(e -> {
        	
        	if(rag_LangChain.getEmbeddingsAll()==null) {rag_LangChain.createEmbeddings(rag_LangChain.textSegments);}
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	ta_display.setText("");
        	String seperator ="\n\n------------------- Embedding Vector 3 and Embedding Dimension is: "
        			+rag_LangChain.embeddingModel.dimension() + " ------------------------\n\n";

        	ta_display.appendText(seperator);
        	ta_display.appendText(rag_LangChain.getEmbedding(2).toString());   	
        });
        
        //////////////////
        bt_embeddingall.setOnAction(e -> {
        	//String seperator ="\n\n------------------- Embedding Vector 3  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
        	if(rag_LangChain.getEmbeddingsAll()==null) {rag_LangChain.createEmbeddings(rag_LangChain.textSegments);}
        	ta_display.setText("");
        	int i=0;
        	for (Embedding embedding:rag_LangChain.getEmbeddingsAll()) {
        		String seperator ="\n\n------------------- Embedding Vector  "+ i + " ------------\n";
        		ta_display.appendText(seperator);
        		ta_display.appendText(embedding.toString());
        		i++;
        	} 
        });
        
        //////////////
        bt_storevector.setOnAction(e -> {
        	rag_LangChain.createInMemoryEmbeddingStoreData();
        });
        
        
        bt_vectorsearch.setOnAction(e -> {
        	ta_display.setText("");
        	String prompt=ta_prompt.getText();
        	System.out.println("ta_prompt.getText() is: "+ prompt);
        	String promptVector=rag_LangChain.getVector(prompt);
			String seperator = "\n---------------- embedding prompt is: \n";
 			ta_display.appendText(seperator);
 			ta_display.appendText(promptVector);

        	
        });
        
        
        ///////////////////
        bt_vectormatch.setOnAction(e -> {
        	ta_display.setText("");
        	String prompt=ta_prompt.getText();
        	System.out.println("ta_prompt.getText() is: "+ prompt);
        	rag_LangChain.vectorSearch(prompt);
        	//EmbeddingMatch<TextSegment> em = rag_LangChain.matches;
        	int  i=1;
     	    for(EmbeddingMatch<TextSegment> em: rag_LangChain.matches) {
     			String seperator = "\n---------------- embedding Match " + i + " and Score is: \""+em.score()+"\"\n";
     			ta_display.appendText(seperator);
     			ta_display.appendText(em.embedded().text());

     			i++;
     	    }
        });
        
        //////////////////////////////
        bt_submit.setOnAction(e -> {
        	ta_display.setText("");
        	String prompt=ta_prompt.getText();
        	System.out.println("ta_prompt.getText() is: "+ prompt);
        	//if(rag_LangChain.matches==null) {
        		rag_LangChain.vectorSearch(prompt);
        	//}
        	//EmbeddingMatch<TextSegment> em = rag_LangChain.matches;
        	String seperator ="\n\n----------------------- returning Message from LLM: "+ rag_LangChain.chatModel.getModelName() + "------------------------\n\n";
        	
        	String seperator_Prompt ="\n\n-----------------------Rag Prompt for LLM Model: \""+rag_LangChain.chatModel.getModelName() +"\" and the Embedding Model:" 
        					+rag_LangChain.embeddingModel+ " ---------------\n\n";
         	
        	String Ragprompt = rag_LangChain.getRagPrompt(prompt);
        	
        	ta_display.appendText(seperator_Prompt);
        	ta_display.appendText(Ragprompt);
        	
 	
        	try {
        	String result = rag_LangChain.getQueryResult(Ragprompt);
        	ta_display.appendText(seperator);
        	ta_display.appendText(result);
        	}catch (Exception e1) {}
        });
        
        /////////////////////////////////
        bt_clear.setOnAction(e -> {  
        	
        	//vb_generative.getChildren().clear();
        	ta_display.setText("");
        	ta_prompt.setText("");

        });
        
        
        
        ComboBox_prompt.getItems().addAll("plsql","java","general");
        ///////////////////////////
        
        ComboBox_prompt.setOnAction((event) -> {

        			
            int selectedIndex = ComboBox_prompt.getSelectionModel().getSelectedIndex();
            Object selectedItem = ComboBox_prompt.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ComboBox.getValue(): " + ComboBox_prompt.getValue());
            switch(selectedIndex) {
			case 0: 
				ta_display.clear();
				ta_display.setText(plsql_template);
				break;
			case 1:
				ta_display.clear();
				ta_display.setText(java_template);
				break;
			case 2:
				ta_display.clear();
				ta_display.setText(general_template);
				break;


            }
            
        	
        });
        
        bt_template.setOnAction(e -> {  
        	
        	String bt_template=null;
        	bt_template=ta_display.getText();
        	rag_LangChain.setPromptTempalte(bt_template);
        	//vb_generative.getChildren().clear();
        	ta_display.setText("");
        	ta_prompt.setText("");

        });
        
    }
}
