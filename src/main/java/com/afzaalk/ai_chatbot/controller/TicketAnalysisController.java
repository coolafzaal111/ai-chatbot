package com.afzaalk.ai_chatbot.controller;

import com.afzaalk.ai_chatbot.model.SuggestedResponse;
import com.afzaalk.ai_chatbot.model.TicketAnalysis;
import com.afzaalk.ai_chatbot.service.TicketAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketAnalysisController {

    @Autowired
    private TicketAnalysisService ticketAnalysisService;
    
    
    @PostMapping("/analyze")
    public Map<String, Object> analyzeTicket(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-5") String model
    )
    {
        // Extract the ticket text from request body
        String ticketText = request.get("ticketText");

        try {
            // STEP 1: ANALYZE TICKET 
            TicketAnalysis analysis = ticketAnalysisService.analyzeTicket(
                    ticketText,
                    provider,
                    model
            );

            System.out.println(analysis.getPriority());

            // STEP 2: CHECK FOR CRITICAL SITUATION 
            if (analysis.getPriority().equals(TicketAnalysis.Priority.CRITICAL)
                    && analysis.getSentiment().equals(TicketAnalysis.Sentiment.ANGRY)) {

                // GENERATE URGENT RESPONSES 
                List<SuggestedResponse> responses = ticketAnalysisService
                        .generateUrgentResponses(analysis, provider, model);

                // Return enhanced response with suggested messages
                return Map.of(
                        "success", true,
                        "analysis", analysis,
                        "responses", responses,  // Additional field for urgent cases
                        "priorityColor", ticketAnalysisService.getPriorityColor(analysis.getPriority()),
                        "sentimentEmoji", ticketAnalysisService.getSentimentEmoji(analysis.getSentiment())
                );
            }

            // RETURN STANDARD ANALYSIS
            return Map.of(
                    "success", true,
                    "analysis", analysis,
                    "priorityColor", ticketAnalysisService.getPriorityColor(analysis.getPriority()),
                    "sentimentEmoji", ticketAnalysisService.getSentimentEmoji(analysis.getSentiment())
            );

        } catch (Exception e) {

            // Return structured error response instead of throwing exception.
            return Map.of(
                    "success", false,
                    "error", "Failed to analyze ticket: " + e.getMessage()
            );
        }

    }
}