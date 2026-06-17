package com.assignment.rewardmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.entity.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {

    List<TransactionRecord> findByCustomerOrderByTransactionDateDesc(Customer customer);

    @Query("select t from TransactionRecord t "
            + "where t.customer = :customer and t.transactionDate between :start and :end "
            + "order by t.transactionDate desc")
    List<TransactionRecord> findByCustomerAndDateRange(@Param("customer") Customer customer,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
