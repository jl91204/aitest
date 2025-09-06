package com.ai.aitest.controller;

import com.ai.aitest.*;
import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class MyPlayGround_Controller {

    @FXML
    private VBox vb_generative;

    @FXML
    private Label  lb_completionTokens;
    
    @FXML
    private Label  lb_promptTokens;
    
    @FXML
    private Button  bt_chatHistory;
    
    @FXML
    private TextFlow myTextFlow;
    
    @FXML
    private TextArea ta_prompt;
    
    @FXML
    private TextArea  ta_generative;
    
    @FXML
    private Button bt_submit;
    
    @FXML
    private Button bt_clear;
    
    @FXML
    private ComboBox<String> myComboBox_model;

    @FXML
    private ComboBox<String> myComboBox_example;
    
    @FXML
    public void initialize() {
    	
    	//ChatCohere 
    	
        Text text = new Text("Chat \n");
        text.setFont(Font.font("Verdana", 25));
        Text text1 = new Text(" To get Started, select a model and a preset prompt example. "      		+ "Then, refine the prompts and parameters to fit your use cases. \n");
        text.setFont(Font.font("Verdana", 15));
        Text text2 = new Text(" Model"+"\t\t\t\t\t\t"+"Example");
        text2.setFont(Font.font("Verdana", 15));
          
          //myTextFlow.getChildren().addAll(text,text1);
      	//TextFlow text_flow = new TextFlow();
      	myTextFlow.getChildren().add(text);
      	myTextFlow.getChildren().add(text1);
      	myTextFlow.getChildren().add(text2);
      	//myTextFlow.getChildren().add(text1);

      	
       
        myComboBox_model.getItems().addAll("cohere.command-a-03-2025 v1.0",  "cohere.command-r-08-2024 v1.7","meta.llama-3.2-90b-vision-instruct","meta.llama-3.3-70b-instruct");
        myComboBox_example.getItems().addAll("Generate a job description", "Generate a prodcut pitch", "Generate an email", "Rewrite instructions with steps","Summarize a blog post");
        
        myComboBox_model.setOnAction((event) -> {
            int selectedIndex = myComboBox_model.getSelectionModel().getSelectedIndex();
            Object selectedItem = myComboBox_model.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ComboBox.getValue(): " + myComboBox_model.getValue());
            switch(selectedIndex) {
			case 0: 
				MyApp.setCurrentChatModel("cohere");
				//MyApp.chatCohere.setModelName("cohere.command-a-03-2025");
				MyApp.getCurrentChatModel().setModelName("cohere.command-a-03-2025");
				break;
			case 1:
				MyApp.setCurrentChatModel("cohere");
				MyApp.getCurrentChatModel().setModelName("cohere.command-r-08-2024");
				break;
			case 2:
				MyApp.setCurrentChatModel("llama");
				MyApp.getCurrentChatModel().setModelName("meta.llama-3.2-90b-vision-instruct");
				break;
			case 3:
				MyApp.setCurrentChatModel("llama");
				MyApp.getCurrentChatModel().setModelName("meta.llama-3.3-70b-instruct");
				break;

            }
            
        });
        
        
        myComboBox_example.setOnAction((event) -> {
            int selectedIndex = myComboBox_example.getSelectionModel().getSelectedIndex();
            Object selectedItem = myComboBox_example.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ComboBox.getValue(): " + myComboBox_example.getValue());
            
            String jd="Generate a job description for a data visualization expert with the following three qualifications only:\r\n"
            		+ "1) At least 5 years of data visualization expert\r\n"
            		+ "2) A great eye for details\r\n"
            		+ "3) Ability to create original visualizations\r\n"
            		+ "";
            String productPitch="Generate a product pitch for a USB connected compact microphone that can record surround sound. The "
            		+ "microphone is most useful in recording music or conversations. The microphone can also be useful for "
            		+ "recording podcasts.\r\n"          		+ "";
             
            String email = "As a corporate vice president, generate an email congratulating a team "
            		+ "that has just shipped a new cloud service. Emphasize the great positive impact "
            		+ "the new service will have on the productivity of their customers.";
            String rewrite = "Rewrite the following steps as a numbered list:\r\n"
            		+ "First, reboot the computer to make sure you have a clean memory footprint. \n"
            		+ "Next, click the main menu, and select settings. In the search box of the settings app, type \"updates\" and click search. Select the search results "
            		+ "titled \"Software updates\". In the Software updates screen, click the \"Check for updates\" link. If no updates are available you're done. If updates are available "
            		+ "for your computer, click \"Proceed with this update\" link. Read the update description and confirm "
            		+ " the installation by clicking \"Confirm\". The installation might take several minutes and might require"
            		+ " the computer to restart several times during the installation. If restart happens, you are asked to login to the computer "
            		+ "after each restart. After the process completes, you get a summary of the installed software.";
            String summarize= "Provide a short summary of the following blog post in a bulleted list:\r\n"
            		+ "Oracle's strategy is built around the reality that enterprises work with AI through three different modalities: Infrastructure, models and services, and within applications.\n"
            		+ "First, we provide a robust infrastructure for training and serving models at scale. Through our partnership with NVIDIA, we can give customers superclusters,"
            		+ " which are powered by the latest GPUs in the market connected together with an ultra-low-latency RDMA over converged ethernet (RoCE) network. This solution provides a highly performant, cost-effective"
            		+ " method for training generative AI models at scale. Many AI startups like Adept and MosaicML are building their products directly on OCI.\r\n"
            		+ "Second, we provide easy-to-use cloud services for developers and scientists to utilize in fully managed implementations.\n"
            		+ " We're enabling new generative AI services and business functions through our partnership with Cohere, a leading generative AI company for enterprise-grade large language models (LLMs). Through our partnership with "
            		+ "Cohere, we’re building a new generative AI service. This upcoming AI service, OCI Generative AI, enables OCI customers to add generative AI capabilities to their own applications and workflows through simple APIs.\n"
            		+ "Third, we embed generative models into the applications and workflows that business users use every day. Oracle plans to embed generative AI from Cohere into its Fusion,\n"
            		+ " NetSuite, and our vertical software-as-a-service (SaaS) portfolio to create solutions that provide organizations with the full power of generative AI immediately. Across industries, "
            		+ "Oracle can provide native generative AI-based features to help organizations automate key business functions, improve decision-making, and enhance customer experiences. For example, "
            		+ "in healthcare, Oracle Cerner manages billions of electronic health records (EHR). Using anonymized  data, Oracle can create generative models adapted to the healthcare domain, such as automatically"
            		+ " generating a patient discharge summary or a letter of authorization for medical insurance. Oracle's generative AI offerings span applications to infrastructure and provide the highest"
            		+ " levels of security, performance, efficiency, and value.";
            
           
            switch(selectedIndex) {
			case 0: 
				ta_prompt.setText(jd);
				break;
			case 1:
				ta_prompt.setText(productPitch);
				break;
			case 2:
				ta_prompt.setText(email);
				break;
			case 3:
				ta_prompt.setText(rewrite);
				break;
			case 4:
				ta_prompt.setText(summarize);
				break;

            }
            
        });
        

       
        bt_submit.setOnAction(e -> { 
        	
        	String seperator ="\n\n----------------------- returning Message from LLM: "+ MyApp.getCurrentChatModel().getModelName() + "------------------------\n\n";
        	
        	String seperator_Prompt ="\n\n----------------------- A New Prompt for LLM: "+MyApp.getCurrentChatModel().getModelName()+"------------------------\n\n";
        	
        	
        	String s = ta_prompt.getText();
        	
        	MyApp.getCurrentChatModel().setPrompt(s);
        	
        	ta_generative.appendText(seperator_Prompt);
        	ta_generative.appendText(s);
        	
        	myComboBox_example.setValue(null);
        	
        	try {
        	String s1= MyApp.getCurrentChatModel().chat();
        	ta_generative.appendText(seperator);
        	ta_generative.appendText(s1);
        	}catch (Exception e1) {}
        	
        	
        	
        	
        	/*
        	TextArea ta1 = textarea1();
        	ta1.setWrapText(true);
        	//ta1.setPrefWidth(300);
        	ta1.setPrefHeight(2);
        	//ta1.setMinWidth(200);
        	ta1.setMinHeight(1);
        	ta1.setMaxWidth(900);
        	ta1.setMaxHeight(3);
        	ta1.setEditable(false);
        	vb_generative.getChildren().add(ta1);

        	
        	TextArea ta2 = textarea2();
        	ta2.setEditable(false);
        	ta2.setWrapText(true);
        	//ta1.setPrefWidth(300);
        	ta1.setPrefHeight(200);
        	//ta1.setMinWidth(200);
        	ta1.setMinHeight(200);
        	ta1.setMaxWidth(900);
        	ta1.setMaxHeight(300);
        	ta2.setEditable(false);

        	vb_generative.getChildren().add(ta2);
        	*/
        	
        	ta_prompt.setText("");
        	
        	lb_promptTokens.setText(MyApp.getCurrentChatModel().getPromptTokens());
        	lb_completionTokens.setText(MyApp.getCurrentChatModel().getCompletionTokens());
        });
        
        bt_clear.setOnAction(e -> {  
        	
        	//vb_generative.getChildren().clear();
        	ta_generative.setText("");
        	ta_prompt.setText("");
        	lb_promptTokens.setText("");
        	lb_completionTokens.setText("");
   	
        });
        
        
    
        
        
    }
    
    public TextArea textarea1 () {
    	
    	TextArea prompt_text = new TextArea();
    	prompt_text.setStyle(
        "-fx-font-size: 22px;" +
        "-fx-text-fill: \"0x0000FF\";");
    	prompt_text.appendText(ta_prompt.getText());
    	return prompt_text;
    }
    
   public TextArea textarea2 () {
	   TextArea generative_text = new TextArea();
	   try {
	      
       MyApp.getCurrentChatModel().setPrompt(ta_prompt.getText());
       String s= MyApp.getCurrentChatModel().chat();
       
       generative_text.setStyle(
               "-fx-font-size: 20px;" +
               "-fx-text-fill: black;");    
       generative_text.appendText(s);
	   }catch(Exception e) {}
	   return generative_text;
    }
   
 
	   
	

   
}
