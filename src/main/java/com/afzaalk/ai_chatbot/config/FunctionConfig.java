package com.afzaalk.ai_chatbot.config;

import com.afzaalk.ai_chatbot.model.*;
import com.afzaalk.ai_chatbot.service.OrderService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Configuration class that defines AI-callable tools/functions.
 */
@Service
public class FunctionConfig {

    @Autowired
    private OrderService orderService;

    /**
     * Tool: Get Order Status
     */
    @Tool(description = "Get order status, tracking info, and delivery estimate by order ID")
    public OrderResponse getOrderStatus(
            @ToolParam(description = "The order ID to check")
            String orderId
    ) {
        System.out.println("🔧 AI used tool: getOrderStatus(" + orderId + ")");
        return orderService.getOrderStatus(orderId);
    }

}