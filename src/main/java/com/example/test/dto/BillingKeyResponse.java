package com.example.test.dto;

import lombok.Data;

@Data
public class BillingKeyResponse {
    private String mId;
    private String customerKey;
    private String authenticatedAt;
    private String method;
    private String billingKey;
    private Card card;

    @Data
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }
}
