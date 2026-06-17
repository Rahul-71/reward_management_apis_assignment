package com.assignment.rewardmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.rewardmanagement.dto.response.RewardResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.repository.CustomerRepository;
import com.assignment.rewardmanagement.repository.TransactionRecordRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Clark");
        customer.setEmail("clark@gmail.com");
    }

    @Test
    void getRewardPoints_customerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.getRewardPointsForCustomer(99L));
    }

    @Test
    void getRewardPoints_noTransactions() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerAndDateRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        RewardResponse response = rewardService.getRewardPointsForCustomer(1L);

        assertEquals(0, response.getTotalRewardPoints());
        assertTrue(response.getMonthlyRewardPoints().isEmpty());
    }

    @Test
    void getRewardPoints_transactionAbove100() {
        // $120 → 2*(120-100) + 50 = 90 points
        TransactionRecord trans = buildTransaction(new BigDecimal("120.00"), LocalDateTime.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerAndDateRange(any(), any(), any()))
                .thenReturn(List.of(trans));

        RewardResponse response = rewardService.getRewardPointsForCustomer(1L);

        assertEquals(90, response.getTotalRewardPoints());
    }

    @Test
    void getRewardPoints_transactionBetween50And100() {
        // $75 → 75-50 = 25 points
        TransactionRecord trans = buildTransaction(new BigDecimal("75.00"), LocalDateTime.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerAndDateRange(any(), any(), any()))
                .thenReturn(List.of(trans));

        RewardResponse response = rewardService.getRewardPointsForCustomer(1L);

        assertEquals(25, response.getTotalRewardPoints());
    }

    @Test
    void getRewardPoints_transactionBelow50() {
        // $45 → 0 points
        TransactionRecord trans = buildTransaction(new BigDecimal("45.00"), LocalDateTime.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerAndDateRange(any(), any(), any()))
                .thenReturn(List.of(trans));

        RewardResponse response = rewardService.getRewardPointsForCustomer(1L);

        assertEquals(0, response.getTotalRewardPoints());
    }

    @Test
    void getRewardPoints_multipleMonths() {
        // April: $120 = 90 pts, May: $75 = 25 pts → total 115
        TransactionRecord april = buildTransaction(new BigDecimal("120.00"),
                LocalDateTime.of(2026, 4, 5, 10, 0));
        TransactionRecord may = buildTransaction(new BigDecimal("75.00"),
                LocalDateTime.of(2026, 5, 10, 9, 0));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRecordRepository.findByCustomerAndDateRange(any(), any(), any()))
                .thenReturn(List.of(april, may));

        RewardResponse response = rewardService.getRewardPointsForCustomer(1L);

        assertEquals(115, response.getTotalRewardPoints());
        assertEquals(90, response.getMonthlyRewardPoints().get(Month.APRIL));
        assertEquals(25, response.getMonthlyRewardPoints().get(Month.MAY));
    }

    private TransactionRecord buildTransaction(BigDecimal amount, LocalDateTime date) {
        TransactionRecord trans = new TransactionRecord();
        trans.setCustomer(customer);
        trans.setAmount(amount);
        trans.setTransactionDate(date);
        return trans;
    }
}
