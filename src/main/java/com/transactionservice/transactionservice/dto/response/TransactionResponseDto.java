package com.transactionservice.transactionservice.dto.response;

import lombok.Data;

@Data
public class TransactionResponseDto {
    private String id;
    private String description;
    private double amount;
    private String creditCard;
    private String recipientCreditCard;
    private Boolean isIncome;
    private String category;
    private String createdAt;
    private String updatedAt;
}
