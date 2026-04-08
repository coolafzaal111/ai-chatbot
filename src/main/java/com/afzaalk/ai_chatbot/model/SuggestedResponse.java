package com.afzaalk.ai_chatbot.model;


public class SuggestedResponse {

    private String tone;
    private String responseText;
    private int estimatedReadingTime;
    public SuggestedResponse() {
    }

    public SuggestedResponse(String tone, String responseText, int estimatedReadingTime) {
        this.tone = tone;
        this.responseText = responseText;
        this.estimatedReadingTime = estimatedReadingTime;
    }


    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public int getEstimatedReadingTime() {
        return estimatedReadingTime;
    }

    public void setEstimatedReadingTime(int estimatedReadingTime) {
        this.estimatedReadingTime = estimatedReadingTime;
    }
}