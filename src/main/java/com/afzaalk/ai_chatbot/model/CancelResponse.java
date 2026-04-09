package com.afzaalk.ai_chatbot.model;

public record CancelResponse(
        boolean success,
        String message,
        String orderId,
        double refundAmount
) {}
