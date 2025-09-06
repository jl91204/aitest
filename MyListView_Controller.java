package com.ai.aitest.controller;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.*;
import com.ai.aitest.*;

public class MyListView_Controller {

    @FXML
    private ListView<String> myListView;
    
    @FXML
    private ImageView imageView;
    
    @FXML
    private SplitPane splitPane;
    
    @FXML
    private AnchorPane AnchorPane_PlayGround;
    
    @FXML
    private BorderPane chatText;
    
    @FXML
    private BorderPane langchain_pane;
    
    @FXML
    private BorderPane selectai_pane;
    
    @FXML
    private BorderPane vectorsearch_pane;
    
    @FXML
    private BorderPane llmlocal_pane;
    
    @FXML
    private BorderPane ailanguage_pane;
    
    @FXML
    private BorderPane aiImage_pane;
    
    @FXML
    private BorderPane objectStorage_pane;
    
    @FXML
    private BorderPane imagevectorsearch_pane;
    
   // String[] AIServices = {"Generative AI playground","LangChain Rag App","Select AI","Vector with RAG","Language AI","Image AI"};
    ObservableList<String> items =FXCollections.observableArrayList (
    		"Generative AI playground","LangChain RAG Assistant","Select AI",
    		"Textual Vector Search ","OLLAMA PlayGround", "Object Storage");
    String currentService;
    
    
    public void initialize() {
    	
    	myListView.getItems().addAll(items);
    	myListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    		
    		public void changed(ObservableValue<? extends String> arg0,String arg1,String arg2){
    			
    			currentService=myListView.getSelectionModel().getSelectedItem();
    			System.out.println(currentService);
    			
    			int i = myListView.getSelectionModel().getSelectedIndex();
    			switch(i) {
    			case 0: 
    			//AnchorPane_PlayGround.getChildren().clear();
    				splitPane.getItems().set(1, AnchorPane_PlayGround);;
    			//splitPane.getItems().set(1, AnchorPane_PlayGround);
					break;
    			case 1:
    			//AnchorPane_PlayGround.getChildren().clear();
    			//AnchorPane_PlayGround.getChildren().addAll(vbox);
    				splitPane.getItems().set(1, langchain_pane);
    				break;
    			case 2:
    				splitPane.getItems().set(1, selectai_pane);
    				break;
    			
    			case 3:
    				splitPane.getItems().set(1, vectorsearch_pane);
    				break;
    				
    			case 4:
    				splitPane.getItems().set(1, llmlocal_pane);
    				break;
    				
  
    			case 5:
    				splitPane.getItems().set(1, objectStorage_pane);
    				break;
    				
    			
    			}
    			
    		}
    	});
    	
    	//imageView.fitHeightProperty().bind( imageView.getParent().layoutBoundsProperty().get()).heightProperty());
    	
    }
    
}
