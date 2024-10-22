package com.transactionservice.transactionservice.service.impl;

import com.opencsv.CSVWriter;
import com.transactionservice.transactionservice.dto.request.TransactionRequestDto;
import com.transactionservice.transactionservice.dto.response.BaseResponseDto;
import com.transactionservice.transactionservice.dto.response.TransactionResponseDto;
import com.transactionservice.transactionservice.entity.Transaction;
import com.transactionservice.transactionservice.repository.TransactionRepository;
import com.transactionservice.transactionservice.service.TransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public BaseResponseDto createTransaction(TransactionRequestDto request) {
        try {
            // Buat object Transaction dari request DTO
            Transaction transaction = new Transaction();
            BeanUtils.copyProperties(request, transaction);

            // Simpan transaksi pengirim
            transaction = transactionRepository.save(transaction); // simpan transaksi pengirim

            // Cek apakah kategori adalah transfer
            if ("transfer".equalsIgnoreCase(transaction.getCategory()) && transaction.getRecipientCreditCard() != null) {
                // Buat transaksi untuk penerima
                Transaction recipientTransaction = new Transaction();
                recipientTransaction.setDescription("Transfer from " + transaction.getDescription());
                recipientTransaction.setAmount(transaction.getAmount());
                recipientTransaction.setCreditCard(transaction.getRecipientCreditCard());
                recipientTransaction.setRecipientCreditCard(transaction.getCreditCard());
                recipientTransaction.setIsIncome(true); // Untuk penerima, ini adalah pemasukan
                recipientTransaction.setCategory(transaction.getCategory());

                // Simpan transaksi penerima
                recipientTransaction = transactionRepository.save(recipientTransaction);
            }

            // Buat response DTO dengan data dari entity yang sudah tersimpan
            TransactionResponseDto responseDto = new TransactionResponseDto();
            BeanUtils.copyProperties(transaction, responseDto);

            // Bungkus response ke dalam BaseResponseDto
            return BaseResponseDto.builder()
                    .status(HttpStatus.CREATED)
                    .description("Transaction successful")
                    .data(Collections.singletonMap("transaction", responseDto))
                    .build();
        } catch (Exception e) {
            return BaseResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .description("Error creating transaction")
                    .build();
        }
    }


    @Override
    public BaseResponseDto getTransactionById(String transactionId) {
        try {
            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if (optionalTransaction.isPresent()) {
                Transaction transaction = optionalTransaction.get();
                TransactionResponseDto responseDto = new TransactionResponseDto();
                BeanUtils.copyProperties(transaction, responseDto);

                return BaseResponseDto.builder()
                        .status(HttpStatus.OK)
                        .description("Transaction found")
                        .data(Collections.singletonMap("transaction", responseDto))
                        .build();
            } else {
                return BaseResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .description("Transaction not found")
                        .build();
            }
        } catch (Exception e) {
            return BaseResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .description("Error fetching transaction")
                    .build();
        }
    }

    @Override
    public BaseResponseDto getAllTransactions(String creditCard) {
        try {
            List<Transaction> transactions = transactionRepository.findByCreditCard(creditCard);
            List<TransactionResponseDto> responseList = transactions.stream().map(transaction -> {
                TransactionResponseDto responseDto = new TransactionResponseDto();
                BeanUtils.copyProperties(transaction, responseDto);
                return responseDto;
            }).collect(Collectors.toList());

            return BaseResponseDto.builder()
                    .status(HttpStatus.OK)
                    .description("Transactions retrieved successfully")
                    .data(Collections.singletonMap("transactions", responseList))
                    .build();
        } catch (Exception e) {
            return BaseResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .description("Error fetching transactions")
                    .build();
        }
    }


    @Override
    public BaseResponseDto exportTransactionsByCreditCard(String creditCard) {
        try {
            List<Transaction> transactions = transactionRepository.findByCreditCard(creditCard);
            if (transactions.isEmpty()) {
                return BaseResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .description("No transactions found for this credit card.")
                        .data(Collections.emptyMap())
                        .build();
            }

            // Buat CSV dari transaksi
            String base64Csv = generateCsvFromTransactions(transactions);

            return BaseResponseDto.builder()
                    .status(HttpStatus.OK)
                    .description("Transactions exported successfully.")
                    .data(Collections.singletonMap("fileData", base64Csv))
                    .build();
        } catch (Exception e) {
            return BaseResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .description("Error exporting transactions: " + e.getMessage())
                    .data(Collections.emptyMap())
                    .build();
        }
    }

    private String generateCsvFromTransactions(List<Transaction> transactions) throws IOException {
        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);

        // Header CSV
        String[] header = { "ID", "Description", "Amount", "CreditCard", "RecipientCreditCard", "IsIncome", "Category", "CreatedAt" };
        csvWriter.writeNext(header);

        for (Transaction transaction : transactions) {
            String[] data = {
                    transaction.getId(),
                    transaction.getDescription(),
                    String.valueOf(transaction.getAmount()),
                    transaction.getCreditCard(),
                    transaction.getRecipientCreditCard(),
                    String.valueOf(transaction.getIsIncome()),
                    transaction.getCategory(),
                    transaction.getCreatedAt().toString()
            };
            csvWriter.writeNext(data);
        }

        csvWriter.close();
        String csvContent = sw.toString();
        return Base64.getEncoder().encodeToString(csvContent.getBytes());
    }
}

