package com.assignment.rewardmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {

    List<TransactionRecord> findByCustomerOrderByTransactionDateDesc(Customer customer);
}
