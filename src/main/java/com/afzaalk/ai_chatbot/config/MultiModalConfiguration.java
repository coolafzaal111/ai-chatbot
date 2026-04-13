package com.afzaalk.ai_chatbot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.afzaalk.ai_chatbot.advisor.PiiRedactionAdvisor;

@Configuration
public class MultiModalConfiguration {
	
	// Logger for debugging and monitoring bean creation
	private static final Logger log = LoggerFactory.getLogger(MultiModalConfiguration.class);

    // GEMINI CONFIGURATION PROPERTIES 
    @Value("${gemini.api.key}")
    private String geminiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.completions.path}")
    private String completionsPath;

    @Value("${gemini.model.name}")
    private String geminiModelName;
    
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)  // Keep last 10 messages
                .build();
    }
    
    
    @Bean("openaiChatClient")
    @Primary
    public ChatClient openaiChatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {

        // ChatClient.create() wraps the model in a client with fluent API
//        ChatClient client = ChatClient.create(openAiChatModel);
    	
    	// ChatClient.create() wraps the model in a client with fluent API
    	ChatClient.Builder builder = ChatClient.builder(openAiChatModel);
    	builder.defaultAdvisors(
                // ADVISOR 1: PII Redaction (Security)
                // Removes sensitive data BEFORE it reaches logging or AI.
                // Must run first to ensure no PII leaks anywhere.
                new PiiRedactionAdvisor(),

                // ADVISOR 2: Simple Logger (Debugging/Monitoring)
                // Runs after PII redaction, so logs are safe.
                new SimpleLoggerAdvisor(),		// Logs requests and responses for debugging

                // ADVISOR 3: Chat Memory (Conversation Context)
                // Automatically manages conversation history.
                // - On request: Adds previous messages to the prompt
                // - On response: Stores the new exchange in memory
                // Uses the injected chatMemory bean for storage.
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        );
    	
    	ChatClient client = builder.build();
    	
        return client;
    }
    
    @Bean("geminiChatClient")
    public ChatClient geminiChatClient(ChatMemory chatMemory) {
        // Create the low-level API client pointing to Gemini's endpoint
        OpenAiApi geminiApi = OpenAiApi.builder()
                .baseUrl(geminiUrl)                    // Gemini's base URL instead of api.openai.com
                .completionsPath(completionsPath)      // Gemini's completions path
                .apiKey(geminiKey)                     // Gemini API key
                .build();
        log.info("Created geminiApi: {}", geminiApi);

        // Create the chat model with Gemini-specific options
        OpenAiChatModel geminiModel = OpenAiChatModel.builder()
                .openAiApi(geminiApi)                  // Use our custom Gemini API client
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(geminiModelName)        // Specify which Gemini model to use
                        .temperature(1.0)              // Controls randomness (0.0 = deterministic, 2.0 = very random)
                        .build())
                .build();
        log.info("Created geminiModel: {}", geminiModel);

        // Wrap the model in a ChatClient for easy interaction
        ChatClient.Builder builder = ChatClient.builder(geminiModel);
        
        builder.defaultAdvisors(
                new PiiRedactionAdvisor(),
                new SimpleLoggerAdvisor(),
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        );
    	
    	ChatClient client = builder.build();
        
        log.info("Created openaiChatClient: {}", client);
        return client;
    }

}
