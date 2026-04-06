package com.afzaalk.ai_chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.afzaalk.ai_chatbot.service.ConversationService;
import com.afzaalk.ai_chatbot.service.ModelService;



@RestController
@RequestMapping("/api")
public class ConversationController {

    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private ConversationService conversationService;

    /**
     * Send a message within a conversation context (with memory).
     * ENDPOINT: POST /api/conversation/{conversationId}
     */
    @PostMapping("/{conversationId}")
    public Map<String, Object> chat(
            @PathVariable String conversationId,
            @RequestHeader(value = "AI-Provider", defaultValue = "openai") String provider,
            @RequestHeader(value = "AI-Model", required = false) String model,
            @RequestBody String message
    ) {

        // Validate: For non-OpenAI providers, model is mandatory
        if (!"openai".equalsIgnoreCase(provider) &&
                (model == null || model.isEmpty())) {
            return Map.of(
                    "error", true,
                    "message", "AI-Model header is required when using provider: " + provider
            );
        }

        // ==================== STEP 1: STORE USER MESSAGE ====================
        // Before calling the AI, save the user's message to conversation history.
        // This ensures the message is included in the context for this and future calls.
        conversationService.addUserMessage(conversationId, message);

        // ==================== STEP 2: RETRIEVE CONVERSATION HISTORY ====================
        // KEY CONCEPT: Token Management
        //
        // LLMs have a maximum "context window" (e.g., GPT-4 has 128K tokens).
        // We can't send infinite history - we must truncate older messages.
        //
        // getRecentMessages() returns messages that fit within token limits,
        // typically keeping the most recent messages and dropping older ones.
        // This is a common strategy called "sliding window" or "truncation".
        List<Message> history = conversationService.getRecentMessages(conversationId);

        log.info("=== REQUEST RECEIVED ===");
        log.info("Provider: {}", provider);
        log.info("Model header: {}", model);
        log.info("Message: {}", message);

        // ==================== STEP 3: GET AI CLIENT ====================
        ChatClient chatClient = modelService.getChatClient(provider);
        log.info("ChatClient class: {}", chatClient.getClass().getName());
        log.info("ChatClient: {}", chatClient);

        // ==================== STEP 4: BUILD PROMPT WITH HISTORY ====================
        // KEY CONCEPT: .messages(history)
        //
        // Unlike the simple ChatController that only sends .user(message),
        // here we use .messages(history) to send the ENTIRE conversation.
        //
        // The history List<Message> contains:
        // - UserMessage objects (what the human said)
        // - AssistantMessage objects (what the AI replied)
        // - Optionally SystemMessage (instructions for the AI)
        //
        // Spring AI's ChatClient will format these correctly for the LLM API.
        var promptSpec = chatClient.prompt().messages(history);

        // ==================== STEP 5: ADD MODEL OPTIONS IF SPECIFIED ====================
        // Optional: Override the default model at runtime
        if (model != null && !model.isEmpty()) {
            promptSpec = promptSpec.options(
                    OpenAiChatOptions.builder()
                            .model(model)
                            .temperature(1.0)
                            .build()
            );
        }

        log.info("Using default model from ChatClient bean");
        log.info("=== CALLING PROMPT ===");

        // ==================== STEP 6: EXECUTE AI REQUEST ====================
        // KEY CONCEPT: ChatResponse vs .content()
        //
        // Previously we used .call().content() to get just the text.
        // Here we use .call().chatResponse() to get the FULL response object.
        //
        // ChatResponse contains:
        // - result: The AI's response (ChatGeneration)
        // - metadata: Token usage, model info, finish reason, etc.
        //
        // This is useful when you need more than just the text output.
        ChatResponse response = promptSpec.call().chatResponse();
        String aiResponse = response.getResult().getOutput().toString();

        log.info("aiResponse = "+ aiResponse);

        // ==================== STEP 7: STORE AI RESPONSE ====================
        // Save the assistant's response to history for future context.
        // Next time the user sends a message, this response will be included.
        conversationService.addAssistantMessage(conversationId, aiResponse);

        // ==================== STEP 8: RETURN RESPONSE WITH METADATA ====================
        // Include useful metadata for debugging and monitoring:
        // - messageCount: How many messages in this conversation
        // - totalTokens: Approximate token usage (for cost tracking)
        return Map.of(
                "conversationId", conversationId,
                "response", aiResponse,
                "messageCount", conversationService.getConversationInfo(conversationId).get("messageCount"),
                "totalTokens", conversationService.getConversationInfo(conversationId).get("totalTokens")
        );
    }



}
