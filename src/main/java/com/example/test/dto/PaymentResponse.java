package com.example.test.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private String type;
    private Card card;
    private Receipt receipt;
    private Checkout checkout;
    private String currency;
    private Long totalAmount;
    private Long balanceAmount;
    private Long suppliedAmount;
    private Long vat;
    private Long taxFreeAmount;
    private String method;
    private String version;

    @Data
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Boolean isInterestFree;
        private String interestPayer;
    }

    @Data
    public static class Receipt {
        private String url;
    }

    @Data
    public static class Checkout {
        private String url;
    }
}
