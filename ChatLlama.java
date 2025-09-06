package com.ai.aitest.util;

import com.ai.aitest.MyApp;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SessionTokenAuthenticationDetailsProvider;
import com.oracle.bmc.generativeaiinference.GenerativeAiInferenceClient;
import com.oracle.bmc.generativeaiinference.model.BaseChatResponse;
import com.oracle.bmc.generativeaiinference.model.ChatChoice;
import com.oracle.bmc.generativeaiinference.model.ChatContent;
import com.oracle.bmc.generativeaiinference.model.ChatDetails;
import com.oracle.bmc.generativeaiinference.model.CohereChatRequest;
import com.oracle.bmc.generativeaiinference.model.DedicatedServingMode;
import com.oracle.bmc.generativeaiinference.model.GenericChatRequest;
import com.oracle.bmc.generativeaiinference.model.GenericChatResponse;
import com.oracle.bmc.generativeaiinference.model.Message;
import com.oracle.bmc.generativeaiinference.model.OnDemandServingMode;
import com.oracle.bmc.generativeaiinference.model.TextContent;
import com.oracle.bmc.generativeaiinference.model.ServingMode;
import com.oracle.bmc.generativeaiinference.model.UserMessage;
import com.oracle.bmc.generativeaiinference.requests.ChatRequest;
import com.oracle.bmc.generativeaiinference.responses.ChatResponse;
import com.oracle.bmc.retrier.RetryConfiguration;
import java.util.ArrayList;
import java.util.List;
//import lombok.extern.slf4j.Slf4j;

/**
 * This class provides an example of how to use OCI Generative AI Service to
 * generate text.
 * <p>
 * The Generative AI Service queried by this example will be assigned:
 * <ul>
 * <li>an endpoint url defined by constant ENDPOINT</li>
 * <li>The configuration file used by service clients will be sourced from the
 * default location (~/.oci/config) and the CONFIG_PROFILE profile will be used.
 * </li>
 * </ul>
 * </p>
 */
//@Slf4j

public class ChatLlama  implements ChatModel {

	/*
							
	GenerativeAiInferenceClient generativeAiInferenceClient;
	
	
	public ChatLlama(GenerativeAiInferenceClient generativeAiInferenceClient) {
		
		this.generativeAiInferenceClient = generativeAiInferenceClient;
	}
	*/
	
	// chatServingmode;

	// GenerativeAiInferenceClient generativeAiInferenceClient;
	/**
	 * The entry point for the example.
	 *
	 * @param args Arguments to provide to the example. This example expects no
	 *             arguments.
	 */
	
	private static ChatLlama instance;

	private ChatLlama() {
	// private constructor to prevent direct instantiation
	}

	public static synchronized ChatLlama getChatLlamaInstance() {
	if (instance == null) {
	instance = new ChatLlama();
	}
	return instance;
	}
	
	
	private String modelName="meta.llama-3.2-90b-vision-instruct";
	private  String prompt = "How do I troubleshoot plsql?";
	private String promptTokens;
	private String completionTokens;
	private String chatHistory;
	
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
		
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPromptTokens() {
		return promptTokens;
	}

	public String getCompletionTokens() {
		return completionTokens;
	}

	public String getChatHistory() {
		return chatHistory;
	}


		



	public String chat() throws Exception{
		
		/**
		final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
		final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		//String prompt = "As a corporate vice president, generate an email congratulating a team that has just shipped a new cloud service. Emphasize the great positive impact the new service will have on the productivity of their customers.";
		// String prompt = "How do I troubleshoot plsql?";
		// Set up Generative AI client with credentials and endpoint
		ClientConfiguration clientConfiguration = ClientConfiguration.builder().readTimeoutMillis(240000)
				.retryConfiguration(RetryConfiguration.NO_RETRY_CONFIGURATION).build();
		// try (GenerativeAiInferenceClient generativeAiInferenceClient = new
		// GenerativeAiInferenceClient(provider,
		// clientConfiguration)) {

		try (GenerativeAiInferenceClient generativeAiInferenceClient = GenerativeAiInferenceClient.builder()
				.configuration(clientConfiguration).endpoint(ENDPOINT).region(REGION).build(provider);) {

			// generativeAiInferenceClient.setEndpoint(ENDPOINT);
			// generativeAiInferenceClient.setRegion(REGION);
			// Build chat request, send, and get response
			
			*/
			
			
			System.out.println("the prompt is: "+ prompt);
			System.out.println("the model name is: "+ modelName);
			

			ServingMode chatServingmode = OnDemandServingMode.builder()
					// .modelId("ocid1.generativeaimodel.oc1.uk-london-1.amaaaaaask7dceyahudlskps3vqcrwiaylpyebnlcyzrryqr3prhwpffchga")
					.modelId(modelName).build();

			ChatContent content = TextContent.builder().text(prompt).build();
			List<ChatContent> contents = List.of(content);

			for (ChatContent c : contents) {
				System.out.println("contents: " + c.toString());
			}

			Message message = UserMessage.builder().content(contents).build();
			// put the message into a List
			List<Message> messages = List.of(message);
			for (Message m : messages) {
				System.out.println("Message: " + m.toString());
			}
			// System.out.println("messages"+messages);
			// create a GenericChatRequest including the current message, and the
			// parameters for the LLM model
			
			GenericChatRequest genericChatRequest = GenericChatRequest.builder().messages(messages).maxTokens(600)
					// .numGenerations(1)
					.frequencyPenalty(1.0).topP(1.0).topK(1).temperature(0.75).isStream(false).seed(1000)
					.presencePenalty(0.0).build();

			// create ChatDetails and ChatRequest providing it with the compartment ID
			// and the parameters for the LLM model
			ChatDetails chatDetails = ChatDetails.builder()

					.chatRequest(genericChatRequest).compartmentId(MyApp.COMPARTMENT_ID).servingMode(chatServingmode).build();
			ChatRequest request = ChatRequest.builder().chatDetails(chatDetails).build();

			// send chat request to the AI inference client
			ChatResponse response = MyApp.getGenerativeAiInferenceClient().chat(request);
			
			
			//System.out.println(response.toString());
			return extractResponseText(response);

			// generateResponse(message,generativeAiInferenceClient);
			// extractResponseText(generateResponse(message,generativeAiInferenceClient));
			//
	
		
	}


	public static String extractResponseText(ChatResponse chatResponse) {
		// get BaseChatResponse from ChatResponse
		BaseChatResponse bcr = chatResponse.getChatResult().getChatResponse();
		// extract text from the GenericChatResponse response type
		// GenericChatResponse represents response from llama models
		if (bcr instanceof GenericChatResponse resp) {
			List<ChatChoice> choices = resp.getChoices();
			
			//while(choices.iterator().hasNext()) {System.out.println(choices.iterator().next());}
			for(ChatChoice c: choices) { System.out.println("choice " + c.getIndex()+" :"+ c.getMessage()); }
			
			List<ChatContent> contents = choices.get(choices.size() - 1).getMessage().getContent();
			ChatContent content = contents.get(contents.size() - 1);
			if (content instanceof TextContent textContent) {
				return textContent.getText();
			}
		}
		throw new RuntimeException("Unexpected ChatResponse");
	}
	
	
	public static void main(String[] args) throws Exception {
		
		 String prompt = "How do I troubleshoot plsql?";;
		
		if (args.length > 0) {
			throw new IllegalArgumentException("This example expects no argument");
		}
		
		//String prompt = "How do I troubleshoot plsql?";
		ChatResponse c=myResponse(prompt);
		
		//extractResponseText(c);
		System.out.println(extractResponseText(c));

	}
	
	
	public static ChatResponse myResponse(String prompt) throws Exception {
	
		   String ENDPOINT = "https://inference.generativeai.uk-london-1.oci.oraclecloud.com";
		   String REGION = "uk-london-1";
		   String CONFIG_LOCATION = "~/.oci/config";
		// TODO: Please update config profile name and use the compartmentId that has
		// policies grant permissions for using Generative AI Service
		  String CONFIG_PROFILE = "LONDON2";
		 String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaaaav5u4xjffhmosvtmqdopcrqqgfb5qzti6jaornpbagsyhyiuclrca"; // ServingMode

		 ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
		final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		//String prompt = "As a corporate vice president, generate an email congratulating a team that has just shipped a new cloud service. Emphasize the great positive impact the new service will have on the productivity of their customers.";
		// String prompt = "How do I troubleshoot plsql?";
		// Set up Generative AI client with credentials and endpoint
		ClientConfiguration clientConfiguration = ClientConfiguration.builder().readTimeoutMillis(240000)
				.retryConfiguration(RetryConfiguration.NO_RETRY_CONFIGURATION).build();
		// try (GenerativeAiInferenceClient generativeAiInferenceClient = new
		// GenerativeAiInferenceClient(provider,
		// clientConfiguration)) {

		try (GenerativeAiInferenceClient generativeAiInferenceClient = GenerativeAiInferenceClient.builder()
				.configuration(clientConfiguration).endpoint(ENDPOINT).region(REGION).build(provider);) {

			// generativeAiInferenceClient.setEndpoint(ENDPOINT);
			// generativeAiInferenceClient.setRegion(REGION);
			// Build chat request, send, and get response

			ServingMode chatServingmode = OnDemandServingMode.builder()
					// .modelId("ocid1.generativeaimodel.oc1.uk-london-1.amaaaaaask7dceyahudlskps3vqcrwiaylpyebnlcyzrryqr3prhwpffchga")
					.modelId("meta.llama-3.3-70b-instruct").build();

			ChatContent content = TextContent.builder().text(prompt).build();
			List<ChatContent> contents = List.of(content);

			for (ChatContent c : contents) {
				System.out.println("contents: " + c.toString());
			}

			Message message = UserMessage.builder().content(contents).build();
			// put the message into a List
			List<Message> messages = List.of(message);
			for (Message m : messages) {
				System.out.println("Message: " + m.toString());
			}
			// System.out.println("messages"+messages);
			// create a GenericChatRequest including the current message, and the
			// parameters for the LLM model
			
			GenericChatRequest genericChatRequest = GenericChatRequest.builder().messages(messages).maxTokens(600)
					// .numGenerations(1)
					.frequencyPenalty(1.0).topP(1.0).topK(1).temperature(0.75).isStream(false).seed(1000)
					.presencePenalty(0.0).build();

			// create ChatDetails and ChatRequest providing it with the compartment ID
			// and the parameters for the LLM model
			ChatDetails chatDetails = ChatDetails.builder()

					.chatRequest(genericChatRequest).compartmentId(COMPARTMENT_ID).servingMode(chatServingmode).build();
			ChatRequest request = ChatRequest.builder().chatDetails(chatDetails).build();

			// send chat request to the AI inference client
			ChatResponse response = generativeAiInferenceClient.chat(request);
			//System.out.println(response.toString());
			return response;

			// generateResponse(message,generativeAiInferenceClient);
			// extractResponseText(generateResponse(message,generativeAiInferenceClient));
			//
			 
		}

	}


}
