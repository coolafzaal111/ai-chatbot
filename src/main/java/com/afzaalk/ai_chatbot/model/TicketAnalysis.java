package com.afzaalk.ai_chatbot.model;


public class TicketAnalysis {


    private String category;
    private String subcategory;
    private Priority priority;
    private Sentiment sentiment;
    private String summary;

    private String suggestedTeam;
    private int estimatedResolutionMinutes;
    private String keyIssues;


    public TicketAnalysis() {
    }

    public TicketAnalysis(String category, String subcategory, Priority priority,
                          Sentiment sentiment, String suggestedTeam,
                          int estimatedResolutionMinutes, String summary, String keyIssues) {
        this.category = category;
        this.subcategory = subcategory;
        this.priority = priority;
        this.sentiment = sentiment;
        this.suggestedTeam = suggestedTeam;
        this.estimatedResolutionMinutes = estimatedResolutionMinutes;
        this.summary = summary;
        this.keyIssues = keyIssues;
    }

    // ==================== GETTERS AND SETTERS ====================
    // Required for JSON serialization/deserialization

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getSuggestedTeam() {
        return suggestedTeam;
    }

    public void setSuggestedTeam(String suggestedTeam) {
        this.suggestedTeam = suggestedTeam;
    }

    public int getEstimatedResolutionMinutes() {
        return estimatedResolutionMinutes;
    }

    public void setEstimatedResolutionMinutes(int estimatedResolutionMinutes) {
        this.estimatedResolutionMinutes = estimatedResolutionMinutes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeyIssues() {
        return keyIssues;
    }

    public void setKeyIssues(String keyIssues) {
        this.keyIssues = keyIssues;
    }

    
    
    // ==================== ENUMS ====================


    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Sentiment {
        HAPPY, NEUTRAL, FRUSTRATED, ANGRY
    }
    
}