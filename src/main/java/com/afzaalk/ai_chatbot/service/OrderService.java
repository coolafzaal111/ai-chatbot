package com.afzaalk.ai_chatbot.service;

import com.afzaalk.ai_chatbot.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock order service to simulate database operations
 * In real application, this would connect to actual database
 */
@Service
public class OrderService {

    // Mock database of orders
    private final Map<String, MockOrder> orders = new HashMap<>();

    public OrderService() {
        // Initialize with sample orders
        orders.put("12345", new MockOrder(
                "12345", "OUT_FOR_DELIVERY", "Today by 6 PM",
                "TRK789XYZ", "John Doe", 2499.00
        ));

        orders.put("67890", new MockOrder(
                "67890", "DELIVERED", "Delivered yesterday",
                "TRK123ABC", "Jane Smith", 1299.00
        ));

        orders.put("11111", new MockOrder(
                "11111", "PROCESSING", "Will ship in 2 days",
                "Not yet assigned", "Alice Johnson", 3999.00
        ));

        orders.put("22222", new MockOrder(
                "22222", "SHIPPED", "Arriving tomorrow",
                "TRK456DEF", "Bob Wilson", 5499.00
        ));

        orders.put("33333", new MockOrder(
                "33333", "CANCELLED", "Cancelled by customer",
                "N/A", "Charlie Brown", 999.00
        ));
    }

    /**
     * Get order status by order ID
     */
    public OrderResponse getOrderStatus(String orderId) {
        MockOrder order = orders.get(orderId);

        if (order == null) {
            return new OrderResponse(
                    orderId,
                    "NOT_FOUND",
                    "Order not found",
                    "N/A",
                    "Unknown",
                    0.0
            );
        }

        return new OrderResponse(
                order.orderId,
                order.status,
                order.estimatedDelivery,
                order.trackingNumber,
                order.customerName,
                order.totalAmount
        );
    }

    // Inner class for mock orders
    private static class MockOrder {
        String orderId;
        String status;
        String estimatedDelivery;
        String trackingNumber;
        String customerName;
        double totalAmount;

        MockOrder(String orderId, String status, String estimatedDelivery,
                  String trackingNumber, String customerName, double totalAmount) {
            this.orderId = orderId;
            this.status = status;
            this.estimatedDelivery = estimatedDelivery;
            this.trackingNumber = trackingNumber;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
        }
    }
}