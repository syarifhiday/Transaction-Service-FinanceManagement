package com.transactionservice.transactionservice.controller;

import com.transactionservice.transactionservice.dto.request.TransactionRequestDto;
import com.transactionservice.transactionservice.dto.response.BaseResponseDto;
import com.transactionservice.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<BaseResponseDto> createTransaction(@RequestBody TransactionRequestDto request) {
        BaseResponseDto response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDto> getTransactionById(@PathVariable("id") String transactionId) {
        BaseResponseDto response = transactionService.getTransactionById(transactionId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/cc/{creditCard}")
    public ResponseEntity<BaseResponseDto> getAllTransactionByCreditCard(@PathVariable String creditCard) {
        BaseResponseDto response = transactionService.getAllTransactions(creditCard);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/export/{creditCard}")
    public ResponseEntity<BaseResponseDto> exportTransactions(@PathVariable String creditCard) {
        BaseResponseDto response = transactionService.exportTransactionsByCreditCard(creditCard);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
