package com.ai.aitest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.ai.aitest.util.*;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.generativeaiinference.GenerativeAiInferenceClient;
import com.oracle.bmc.retrier.RetryConfiguration;

import java.io.IOException;

/**
 * JavaFX App
 */
public class MyApp extends Application {
	
	 static Scene scene;
	public static ChatCohere chatCohere;
	public static ChatLlama  chatLlama;
	
	public static final String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaaaav5u4xjffhmosvtmqdopcrqqgfb5qzti6jaornpbagsyhyiuclrca"; // ServingMode

	private static ChatModel currentChatModel; 
	public static Stage currentStage;
	
	private static GenerativeAiInferenceClient generativeAiInferenceClient;
     
   public static ChatModel getCurrentChatModel() {
		return currentChatModel;
	}
   
   public static GenerativeAiInferenceClient getGenerativeAiInferenceClient() {
	   System.out.println("1 You are in getInstance()");
	   if (generativeAiInferenceClient == null) {
		   generativeAiInferenceClient = createAIClient();
	   }
	   return generativeAiInferenceClient;
	   }

	public static void setCurrentChatModel(String model) {
		if (model=="cohere")
			currentChatModel=chatCohere;
		if (model=="llama")
			currentChatModel=chatLlama;
	}


	public void init() throws Exception{
		System.out.println("2 You are in init()");
	   chatCohere = ChatCohere.getChatCohereInstance();
	   chatLlama = ChatLlama.getChatLlamaInstance();
	   currentChatModel=chatCohere;
   }
   
	

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Layout"), 994, 700);
        stage.setScene(scene);
        stage.setTitle("Generative AI Demo");
        stage.getIcons().add(new Image("http://files.softicons.com/download/application-icons/pixelophilia-icons-by-omercetin/png/32/apple-green.png"));

        this.currentStage=stage;
        stage.show();
    }

    public  static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
     
    }
    
	public static GenerativeAiInferenceClient createAIClient() {
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

}