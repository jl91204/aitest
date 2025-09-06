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
import com.oracle.bmc.generativeaiinference.model.CohereChatResponse;
import com.oracle.bmc.generativeaiinference.model.CohereMessage;
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
public class ChatCohere implements ChatModel{

	// GenerativeAiInferenceClient generativeAiInferenceClient;
	/**
	 * The entry point for the example.
	 *
	 * @param args Arguments to provide to the example. This example expects no
	 *             arguments.
	 */

	private static ChatCohere instance;

	private ChatCohere() {
	// private constructor to prevent direct instantiation
	}

	public static synchronized ChatCohere getChatCohereInstance() {
	if (instance == null) {
	instance = new ChatCohere();
	}
	return instance;
	}

	
		

	
	private String modelName="cohere.command-a-03-2025";
	private String prompt = "How do I troubleshoot plsql?";;
	private String promptTokens;
	private String completionTokens;
	private String chatHistory;
		
	
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String message) {
		this.prompt = message;
	}
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
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

	
	
	public String chat() throws Exception {
		
	
			CohereChatRequest chatRequest = CohereChatRequest.builder().message(prompt).maxTokens(600)
					.temperature((double) 0.5).frequencyPenalty((double) 1).topP((double) 0.75).topK(0).isStream(false)
					.build();

			System.out.println("the prompt is: "+ prompt);
			System.out.println("the model name is: "+ modelName);
			
			ChatDetails details = ChatDetails.builder().servingMode(OnDemandServingMode.builder().modelId(
					//"ocid1.generativeaimodel.oc1.uk-london-1.amaaaaaask7dceyahudlskps3vqcrwiaylpyebnlcyzrryqr3prhwpffchga")
					modelName)
					//"ocid1.generativeaimodel.oc1.uk-london-1.amaaaaaask7dceyarp4fbl4nicr66ibhaqqxg5w77nnzlgmof5hinslboika")
					.build()).compartmentId(MyApp.COMPARTMENT_ID).chatRequest(chatRequest).build();
			
			
			ChatRequest request = ChatRequest.builder().chatDetails(details).build();

			ChatResponse response = MyApp.getGenerativeAiInferenceClient().chat(request);
			
			
			/////////////////
			System.out.println(response.toString());
			
			CohereChatResponse ccr = (CohereChatResponse)(response.getChatResult().getChatResponse());
			promptTokens=ccr.getUsage().getPromptTokens().toString();		
			System.out.println("ccr.getUsage().getPromptTokens(): " + promptTokens);
		
			completionTokens=ccr.getUsage().getCompletionTokens().toString();
			System.out.println("ccr.getUsage().getCompletionTokens(): " + completionTokens);
			
			
			List<CohereMessage> lc=CohereChatRequest.builder().build().getChatHistory();
			System.out.println("CohereChatRequest.builder().build().getChatHistory()--------------------");
			if (lc != null) {
			for (CohereMessage cm :lc) {
				System.out.println(cm.toString());	
			}
			}
			
			System.out.println("CohereChatRequest.builder().build().getChatHistory()--------------------");
			
			List<CohereMessage> lc1 = ccr.getChatHistory();
			if(lc1 !=null) {		
			System.out.println("response.getChatResult().getChatResponse().getChatHistory()--------------------");
			for (CohereMessage cm :lc1) {
				System.out.println(cm.toString());	
			}
			}
			System.out.println("response.getChatResult().getChatResponse().getChatHistory()--------------------");
			
			//chatHistory = ccr.getUsage().getCompletionTokens()
			return ccr.getText();
			
			
			
		}

	
		

	
	
	
	
	
	public static void main(String[] args) throws Exception {
		final String ENDPOINT = "https://inference.generativeai.uk-london-1.oci.oraclecloud.com";
		 final String REGION = "uk-london-1";
		 final String CONFIG_LOCATION = "~/.oci/config";
		// TODO: Please update config profile name and use the compartmentId that has
		// policies grant permissions for using Generative AI Service
		 final String CONFIG_PROFILE = "LONDON2";
		 final String COMPARTMENT_ID = "ocid1.tenancy.oc1..aaaaaaaav5u4xjffhmosvtmqdopcrqqgfb5qzti6jaornpbagsyhyiuclrca";

			final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
			final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
			ClientConfiguration clientConfiguration = ClientConfiguration.builder().readTimeoutMillis(240000)
					.retryConfiguration(RetryConfiguration.NO_RETRY_CONFIGURATION).build();
			
			GenerativeAiInferenceClient generativeAiInferenceClient = GenerativeAiInferenceClient.builder()
					.configuration(clientConfiguration).endpoint(ENDPOINT).region(REGION).build(provider);

		ChatCohere cc=new ChatCohere();
		cc.chat();
		
	}

}
