package com.afzaalk.ai_chatbot.model;

public record ReturnResponse(
        boolean success,
        String message,
        String returnId,
        String returnLabel
) {}
