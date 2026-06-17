package com.assignment.rewardmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.assignment.rewardmanagement.dto.response.RewardResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.repository.CustomerRepository;
import com.assignment.rewardmanagement.repository.TransactionRecordRepository;

@Service
public class RewardServiceImpl implements RewardService {

    private static final Logger log = LoggerFactory.getLogger(RewardServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public RewardServiceImpl(CustomerRepository customerRepository,
            TransactionRecordRepository transactionRecordRepository) {
        this.customerRepository = customerRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Override
    public RewardResponse getRewardPointsForCustomer(Integer customerId) {
        log.info("Calculating rewards for customer id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMonths(3);
        return buildRewardResponse(customer, start, end);
    }

    @Override
    public RewardResponse getRewardPointsForCustomerInRange(Integer customerId, LocalDateTime start, LocalDateTime end) {
        log.info("Calculating rewards for customer id: {} between {} and {}", customerId, start, end);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        return buildRewardResponse(customer, start, end);
    }

    @Override
    public List<RewardResponse> getAllCustomerRewards() {
        log.info("Calculating rewards for all customers");
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMonths(3);

        return customerRepository.findAll()
                .stream()
                .map(customer -> buildRewardResponse(customer, start, end))
                .collect(Collectors.toList());
    }

    private RewardResponse buildRewardResponse(Customer customer, LocalDateTime start, LocalDateTime end) {
        List<TransactionRecord> transactions
                = transactionRecordRepository.findByCustomerAndDateRange(customer, start, end);

        Map<Month, Integer> monthlyPoints = new LinkedHashMap<>();
        for (TransactionRecord transaction : transactions) {
            Month month = transaction.getTransactionDate().getMonth();
            int points = calculateRewards(transaction.getAmount());
            if (monthlyPoints.containsKey(month)) {
                monthlyPoints.put(month, monthlyPoints.get(month) + points);
            } else {
                monthlyPoints.put(month, points);
            }
        }

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return new RewardResponse(customer.getId(), customer.getName(), monthlyPoints, totalPoints);
    }

    /*
    A customer receives 2 points for every dollar spent over $100 in each transaction, 
    plus 1 point for every dollar spent between $50 and $100 in each transaction.
    (e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).
     */
    private int calculateRewards(BigDecimal amount) {
        BigDecimal fifty = new BigDecimal("50");
        BigDecimal hundred = new BigDecimal("100");

        if (amount.compareTo(hundred) > 0) {
            int pointsAbove100 = amount.subtract(hundred).multiply(new BigDecimal("2")).intValue();
            return pointsAbove100 + 50;
        } else if (amount.compareTo(fifty) > 0) {
            return amount.subtract(fifty).intValue();
        }
        return 0;
    }
}
