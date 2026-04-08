package com.afzaalk.ai_chatbot.service;

import com.afzaalk.ai_chatbot.model.SuggestedResponse;
import com.afzaalk.ai_chatbot.model.TicketAnalysis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Service
public class TicketAnalysisService {


    private final ModelService modelService;

    private final ResourceLoader resourceLoader;

    public TicketAnalysisService(ModelService modelService, ResourceLoader resourceLoader) {
        this.modelService = modelService;
        this.resourceLoader = resourceLoader;
    }


    public TicketAnalysis analyzeTicket(String ticketText, String provider, String model) {
    	
        // Get the appropriate ChatClient for the provider
        ChatClient chatClient = modelService.getChatClient(provider);

        // Create prompt from template with ticket text
        Prompt prompt = createTicketAnalysisPrompt(ticketText);

        // Execute AI call and convert response to TicketAnalysis object
        return chatClient
                .prompt(prompt)					// Sets the prompt to send
                .options(OpenAiChatOptions.builder()	// Configures model settings
                        .model(model)
                        .build())
                .call()							// Executes the API call
                .entity(TicketAnalysis.class);	// Converts JSON response to Java object

    }


    public List<SuggestedResponse> generateUrgentResponses(
            TicketAnalysis analysis, String provider, String model)
    {
        ChatClient chatClient = modelService.getChatClient(provider);

        // Create prompt with analysis details
        Prompt prompt = createTicketAnalysisResponsesPrompt(analysis);

        // Execute AI call and convert response to List<SuggestedResponse>
        return chatClient
                .prompt(prompt)
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .call()
                .entity(new ParameterizedTypeReference<List<SuggestedResponse>>() {});

    }


    private Prompt createTicketAnalysisPrompt(String ticketText) {

        // Load template from classpath
        // Location: src/main/resources/templates/ticket-analysis.txt
        String templateContent = loadTemplate("classpath:templates/ticket-analysis.txt");

        // Create Spring AI PromptTemplate
        // PromptTemplate handles {placeholder} substitution
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);

        // Prepare variables - keys must match {placeholders} in template
        // Template has {ticketText} → we provide "ticketText" key
        Map<String, Object> variables = Map.of(
                "ticketText", ticketText
        );

        // Fill template and return Prompt object
        return promptTemplate.create(variables);
    }


    private Prompt createTicketAnalysisResponsesPrompt(TicketAnalysis analysis) {

        // Load template from classpath
        String templateContent = loadTemplate("classpath:templates/ticket-analysis-responses.txt");

        // Create Spring AI PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate(templateContent);

        // Prepare variables from the analysis object
        // Extract specific fields needed by the template
        Map<String, Object> variables = Map.of(
                "category", analysis.getCategory(),
                "issues", analysis.getKeyIssues()
        );

        // Fill template and return Prompt object
        return promptTemplate.create(variables);
    }

    
    private String loadTemplate(String location) {
        try {
            Resource resource = resourceLoader.getResource(
                    location
            );
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ticket analysis template", e);
        }
    }


    public String getPriorityColor(TicketAnalysis.Priority priority) {
        return switch (priority) {
            case CRITICAL -> "#dc3545"; // Red
            case HIGH -> "#fd7e14";     // Orange
            case MEDIUM -> "#ffc107";   // Yellow
            case LOW -> "#28a745";      // Green
        };
    }


    public String getSentimentEmoji(TicketAnalysis.Sentiment sentiment) {
        return switch (sentiment) {
            case HAPPY -> "😊";
            case NEUTRAL -> "😐";
            case FRUSTRATED -> "😤";
            case ANGRY -> "😡";
        };
    }


}