package com.assignment.rewardmanagement.service;

import java.util.List;

import com.assignment.rewardmanagement.dto.request.TransactionRequest;
import com.assignment.rewardmanagement.dto.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse addTransaction(TransactionRequest request);

    List<TransactionResponse> getTransactionsByCustomer(Long customerId);
}
