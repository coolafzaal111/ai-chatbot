package com.afzaalk.ai_chatbot.model;

public record OrderResponse(
        String orderId,
        String status,
        String estimatedDelivery,
        String trackingNumber,
        String customerName,
        double totalAmount
) {}
