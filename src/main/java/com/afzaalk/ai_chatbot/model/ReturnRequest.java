package com.afzaalk.ai_chatbot.model;

// Return Order Tool - Input/Output
public record ReturnRequest(
        String orderId,
        String reason
) {}
