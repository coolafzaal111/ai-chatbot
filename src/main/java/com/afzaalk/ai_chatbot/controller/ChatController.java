package com.afzaalk.ai_chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.afzaalk.ai_chatbot.service.ModelService;



@RestController
@RequestMapping("/api")
public class ChatController {

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private ModelService modelService;

	// SPring AI's fluent api for talking to AI models
	//	private final ChatClient chatClient;
	//	
	//	public ChatController (ChatModel chatModel) {
	//		// wrap ChstModel in ChatCLient using builder pattern
	//		this.chatClient = ChatClient.builder(chatModel).build();
	//	}

	@GetMapping("/test")
	public String test() {
		return "Spring Boot is working!";
	}

	@PostMapping("/chat")
	public String chat(
			@RequestHeader(value = "Ai-Provider", defaultValue = "openai") String provider, 
			@RequestHeader(value = "Ai-Model", defaultValue = "gpt-4o-mini") String model, 
			@RequestBody String messageInput) {

		//		ChatClient chatClient = ChatClient.builder(chatModel).build();
		ChatClient chatClient = modelService.getChatClient(provider);
		if(model != null && !model.isEmpty()) {
			return chatClient
					.prompt()			// start building the prompt
					.user(messageInput)	// add a user message	
					.options(OpenAiChatOptions.builder()
							.model(model)
							.temperature(1.0)
							.maxCompletionTokens(200)
							.build())
					.call()				// execute the request
					.content();			// extracting the text from response
		}

		return chatClient
				.prompt()			// start building the prompt
				.user(messageInput)	// add a user message
				.call()				// execute the request
				.content();			// extracting the text from response

	}


}
