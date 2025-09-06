package com.ai.aitest.util;

public interface ChatModel {
	
	public String getModelName();
	public void setModelName(String modelName);
	
	public void setPrompt(String prompt);
	public String getPrompt();

	public String getPromptTokens();
	public String getCompletionTokens();
	public String getChatHistory();
	
	
	public String chat()  throws Exception;
	

}
