package com.assignment.rewardmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.assignment.rewardmanagement.dto.response.RewardResponse;

public interface RewardService {

    RewardResponse getRewardPointsForCustomer(Long customerId);

    RewardResponse getRewardPointsForCustomerInRange(Long customerId, LocalDateTime start, LocalDateTime end);

    List<RewardResponse> getAllCustomerRewards();
}
