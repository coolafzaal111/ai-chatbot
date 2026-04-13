package com.afzaalk.ai_chatbot.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;

import java.util.regex.Pattern;

/**
 * Advisor that automatically redacts Personally Identifiable Information (PII)
 * from user messages BEFORE they are sent to the AI model.
 */
public class PiiRedactionAdvisor implements CallAdvisor {

    //
    // Regular expressions to identify sensitive data in text.
    // These patterns are compiled once and reused for efficiency.

    /**
     * Pattern to detect email addresses.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
    );

    /**
     * Pattern to detect US phone numbers in various formats.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "\\b(?:\\+?1[-.]?)?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})\\b"
    );

    /**
     * Pattern to detect credit card numbers.
     */
    private static final Pattern CARD_PATTERN = Pattern.compile(
            "\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b"
    );

    /**
     * Pattern to detect US Social Security Numbers.
     */
    private static final Pattern SSN_PATTERN = Pattern.compile(
            "\\b\\d{3}-\\d{2}-\\d{4}\\b"
    );


    /**
     * Returns the name of this advisor for logging and debugging.
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Determines the execution order of this advisor in the chain.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;  // Execute FIRST in chain
    }

    /**
     * The main advisor method - intercepts and processes the AI call.
     */
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

        // STEP 1: EXTRACT ORIGINAL MESSAGE 
        String originalMessage = request.prompt().getUserMessage().getText();

        // STEP 2: REDACT PII 
        String redactedMessage = redactPii(originalMessage);

        // STEP 3: LOG REDACTION (FOR DEBUGGING) 
        // Only log if we actually redacted something.
        if (!originalMessage.equals(redactedMessage)) {
            System.out.println("[PII REDACTION] Sensitive data redacted from user message");
            System.out.println("Original: " + originalMessage);
            System.out.println("Redacted: " + redactedMessage);
        }

        // STEP 4: CREATE MODIFIED REQUEST 
        // All other request properties (system prompt, tools, options) are preserved.
        ChatClientRequest modifiedRequest = request.mutate()		// Creates a builder from the existing request
                .prompt(request.prompt()
                		.augmentUserMessage(redactedMessage))		// Replaces user message content while keeping structure
                .build();											// Creates the new immutable request

        // STEP 5: CONTINUE THE CHAIN 
        return chain.nextCall(modifiedRequest);
    }

    /**
     * Applies all PII detection patterns to redact sensitive information.
     */
    private String redactPii(String text) {
        // Handle null or empty input gracefully
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // Apply each pattern sequentially
        // Order doesn't matter since patterns don't overlap
        String redacted = text;
        redacted = EMAIL_PATTERN.matcher(redacted).replaceAll("[EMAIL_REDACTED]");
        redacted = PHONE_PATTERN.matcher(redacted).replaceAll("[PHONE_REDACTED]");
        redacted = CARD_PATTERN.matcher(redacted).replaceAll("[CARD_REDACTED]");
        redacted = SSN_PATTERN.matcher(redacted).replaceAll("[SSN_REDACTED]");

        return redacted;
    }
    
}