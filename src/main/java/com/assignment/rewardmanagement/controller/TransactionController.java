package com.assignment.rewardmanagement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.rewardmanagement.dto.request.TransactionRequest;
import com.assignment.rewardmanagement.dto.response.TransactionResponse;
import com.assignment.rewardmanagement.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("POST /transactions - adding transaction for customer id: {}", request.getCustomerId());
        TransactionResponse response = transactionService.addTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCustomer(@PathVariable Long customerId) {
        log.info("GET /transactions/customer/{}", customerId);
        List<TransactionResponse> response = transactionService.getTransactionsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}
