package com.assignment.rewardmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.assignment.rewardmanagement.dto.request.TransactionRequest;
import com.assignment.rewardmanagement.dto.response.TransactionResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;
import com.assignment.rewardmanagement.repository.CustomerRepository;
import com.assignment.rewardmanagement.repository.TransactionRecordRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionServiceImpl(CustomerRepository customerRepository,
            TransactionRecordRepository transactionRecordRepository) {
        this.customerRepository = customerRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Override
    public TransactionResponse addTransaction(TransactionRequest request) {
        log.info("Adding transaction for customer id: {}", request.getCustomerId());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        TransactionRecord record = new TransactionRecord();
        record.setCustomer(customer);
        record.setAmount(request.getAmount());
        record.setTransactionDate(request.getTransactionDate());

        TransactionRecord saved = transactionRecordRepository.save(record);
        log.info("Transaction saved with id: {}", saved.getTransactionId());
        return toResponse(saved);
    }

    @Override
    public List<TransactionResponse> getTransactionsByCustomer(Integer customerId) {
        log.info("Fetching transactions for customer id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return transactionRecordRepository.findByCustomerOrderByTransactionDateDesc(customer)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(TransactionRecord record) {
        return new TransactionResponse(
                record.getTransactionId(),
                record.getCustomer().getId(),
                record.getCustomer().getName(),
                record.getAmount(),
                record.getTransactionDate()
        );
    }
}
