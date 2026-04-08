package com.afzaalk.ai_chatbot.controller;

import com.afzaalk.ai_chatbot.service.FunctionCallingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for AI Support Bot with Function Calling capabilities.
 */
@RestController
@RequestMapping("/api/support")
public class FunctionCallingController {

    @Autowired
    private FunctionCallingService functionCallingService;

    /**
     * Basic support chat with single tool: Order Tracking.
     */
    @PostMapping("/chat/basic")
    public Map<String, Object> basicSupportChat(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-4o") String model
    ) {
        String userMessage = request.get("message");

        // Input validation - ensure message is provided
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of(
                    "success", false,
                    "error", "Message cannot be empty"
            );
        }

        try {
            // Call service with basic tool set (order tracking only)
            // The AI will automatically call getOrderStatus() if the user
            // asks about order status, tracking, or delivery
            String response = functionCallingService.chatWithOrderTracking(
                    userMessage, provider, model);

            return Map.of(
                    "success", true,
                    "response", response
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Failed: " + e.getMessage()
            );
        }
    }

    /**
     * Full support chat with multiple tools.
     */
    @PostMapping("/chat/full")
    public Map<String, Object> fullSupportChat(
            @RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "openai") String provider,
            @RequestParam(defaultValue = "gpt-4o") String model
    ) {
        String userMessage = request.get("message");

        // Input validation
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of(
                    "success", false,
                    "error", "Message cannot be empty"
            );
        }

        try {
            // Call service with FULL tool set
            // AI has access to ALL functions and intelligently chooses
            // which one(s) to call based on user's message
            String response = functionCallingService.chatWithFullSupport(
                    userMessage, provider, model);

            return Map.of(
                    "success", true,
                    "response", response
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Failed: " + e.getMessage()
            );
        }
    }
}