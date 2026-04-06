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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afzaalk.ai_chatbot.service.ModelService;

import reactor.core.publisher.Flux;



@RestController
@RequestMapping("/api")
public class StreamController {

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private ModelService modelService;


	@GetMapping(value = "/stream", produces = "text/event-stream")
	public Flux<String> chat(
			@RequestParam(defaultValue = "openai") String provider, 
			@RequestParam(defaultValue = "gpt-4o-mini") String model, 
			@RequestParam String messageInput) {

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
//					.call()				// execute the request
					.stream()
					.content();			// extracting the text from response
		}

		return chatClient
				.prompt()			// start building the prompt
				.user(messageInput)	// add a user message
				.stream()				// execute the request
				.content();			// extracting the text from response

	}


}
