package com.transactionservice.transactionservice.service;

import com.transactionservice.transactionservice.dto.request.TransactionRequestDto;
import com.transactionservice.transactionservice.dto.response.BaseResponseDto;
import com.transactionservice.transactionservice.dto.response.TransactionResponseDto;

import java.util.List;

public interface TransactionService {
    BaseResponseDto createTransaction(TransactionRequestDto request);
//    TransactionResponseDto updateTransaction(String transactionId, TransactionRequestDto request);
//    void deleteTransaction(String transactionId);
    BaseResponseDto getTransactionById(String transactionId);
    BaseResponseDto getAllTransactions(String creditCard);
    BaseResponseDto exportTransactionsByCreditCard(String creditCard);
}

