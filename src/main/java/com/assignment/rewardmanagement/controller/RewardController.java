package com.assignment.rewardmanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.rewardmanagement.dto.response.RewardResponse;
import com.assignment.rewardmanagement.service.RewardService;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private static final Logger log = LoggerFactory.getLogger(RewardController.class);

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public ResponseEntity<List<RewardResponse>> getAllCustomerRewards() {
        log.info("GET /rewards - fetching rewards for all customers");
        return ResponseEntity.ok(rewardService.getAllCustomerRewards());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getRewardPoints(
            @PathVariable Integer customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /rewards/{} startDate={} endDate={}", customerId, startDate, endDate);

        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
            return ResponseEntity.badRequest().body("Both startDate and endDate must be provided together");
        }

        if (startDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body("startDate must not be after endDate");
        }

        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(rewardService.getRewardPointsForCustomerInRange(
                    customerId,
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)));
        }
        return ResponseEntity.ok(rewardService.getRewardPointsForCustomer(customerId));
    }
}
