package com.transactionservice.transactionservice.entity;

import com.transactionservice.transactionservice.dto.AuditingDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Transaction extends AuditingDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String description;
    private double amount;
    private String creditCard;
    private String recipientCreditCard;
    private Boolean isIncome; // income / outcome
    private String category; // makanan / cicilan / gaji
}