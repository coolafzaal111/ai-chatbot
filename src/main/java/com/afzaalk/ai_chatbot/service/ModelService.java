package com.afzaalk.ai_chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ModelService {
	
	private static final Logger log = LoggerFactory.getLogger(ModelService.class);
	
	@Autowired
    @Qualifier("openaiChatClient")
    private ChatClient openaiChatClient;
	
	@Autowired
    @Qualifier("geminiChatClient")
    private ChatClient geminiChatClient;
	
	public ChatClient getChatClient(String provider) {
        log.info("getChatClient called with provider: {}", provider);
        log.info("openaiChatClient instance: {}", openaiChatClient);
        log.info("geminiChatClient instance: {}", geminiChatClient);

        // If no provider specified, default to OpenAI
        if (provider == null || provider.isEmpty()) {
            log.info("Returning default openaiChatClient");
            return openaiChatClient;
        }

        return switch (provider.toLowerCase()) {
            case "openai" -> {
                log.info("Returning openaiChatClient");
                yield openaiChatClient;  // 'yield' returns value from switch expression block
            }
            case "gemini" -> {
                log.info("Returning geminiChatClient");
                yield geminiChatClient;
            }
            default -> throw new IllegalArgumentException(
                    "Unknown provider: " + provider + ". Supported: openai, gemini"
            );
        };
    }

}
