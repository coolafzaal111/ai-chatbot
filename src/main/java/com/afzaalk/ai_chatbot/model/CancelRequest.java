package com.afzaalk.ai_chatbot.model;

// Cancel Order Tool - Input/Output
public record CancelRequest(
        String orderId,
        String reason
) {}
