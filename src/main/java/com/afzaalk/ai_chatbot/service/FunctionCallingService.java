package com.afzaalk.ai_chatbot.service;

import com.afzaalk.ai_chatbot.config.FunctionConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that enables AI function calling capabilities.
 */
@Service
public class FunctionCallingService {

    @Autowired
    private ModelService modelService;
    
    @Autowired
    private FunctionConfig orderSupportTools;

    /**
     * Chat with basic order tracking capability.
     */
    public String chatWithOrderTracking(
            String userMessage,
            String provider,
            String model) {

        ChatClient chatClient = modelService.getChatClient(provider);

        return chatClient
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .user(userMessage)
                .tools(orderSupportTools)  // Enable function calling with this bean
                .call()
                .content();

    }

    /**
     * Chat with full support capabilities and system instructions.
     */
    public String chatWithFullSupport(
            String userMessage,
            String provider,
            String model) {

        ChatClient chatClient = modelService.getChatClient(provider);

        return chatClient
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .system("""
                You are a helpful customer support assistant for an e-commerce company.
                
                You have access to these tools:
                - getOrderStatus: Check order status and tracking
                - cancelOrder: Cancel orders that haven't shipped yet
                - initiateReturn: Start return process for delivered orders
                - checkRefund: Check refund status for cancelled orders
                
                Use these tools to help customers. Be friendly and helpful.
                Always confirm actions before executing them.
                If a tool returns an error, explain it clearly to the customer.
                """)
                .user(userMessage)
                .tools(orderSupportTools)  // Same bean - all 4 tools available
                .call()
                .content();

    }
}