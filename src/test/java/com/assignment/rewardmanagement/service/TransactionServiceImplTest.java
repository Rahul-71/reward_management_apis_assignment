package com.assignment.rewardmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.rewardmanagement.dto.request.TransactionRequest;
import com.assignment.rewardmanagement.dto.response.TransactionResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.repository.CustomerRepository;
import com.assignment.rewardmanagement.repository.TransactionRecordRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Clark");
        customer.setEmail("clark@gmail.com");
    }

    @Test
    void addTransaction_success() {
        TransactionRequest request = new TransactionRequest();
        request.setCustomerId(1L);
        request.setAmount(new BigDecimal("120.00"));
        request.setTransactionDate(LocalDateTime.now());

        TransactionRecord savedRecord = new TransactionRecord();
        savedRecord.setTransactionId(101L);
        savedRecord.setCustomer(customer);
        savedRecord.setAmount(request.getAmount());
        savedRecord.setTransactionDate(request.getTransactionDate());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.save(any(TransactionRecord.class))).thenReturn(savedRecord);

        TransactionResponse response = transactionService.addTransaction(request);

        assertEquals(101L, response.getTransactionId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(new BigDecimal("120.00"), response.getAmount());
    }

    @Test
    void addTransaction_customerNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setCustomerId(99L);
        request.setAmount(new BigDecimal("50.00"));
        request.setTransactionDate(LocalDateTime.now());

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.addTransaction(request));

        verify(transactionRecordRepository, never()).save(any());
    }

    @Test
    void getTransactionsByCustomer_success() {
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(1L);
        record.setCustomer(customer);
        record.setAmount(new BigDecimal("75.00"));
        record.setTransactionDate(LocalDateTime.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerOrderByTransactionDateDesc(customer))
                .thenReturn(List.of(record));

        List<TransactionResponse> result = transactionService.getTransactionsByCustomer(1L);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("75.00"), result.get(0).getAmount());
    }

    @Test
    void getTransactionsByCustomer_customerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionsByCustomer(99L));
    }
}
