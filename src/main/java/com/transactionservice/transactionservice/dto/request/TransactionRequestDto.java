package com.transactionservice.transactionservice.dto.request;

import lombok.Data;

@Data
public class TransactionRequestDto {
    private String description;
    private double amount;
    private String creditCard;
    private String recipientCreditCard;
    private Boolean isIncome;
    private String category;
}
