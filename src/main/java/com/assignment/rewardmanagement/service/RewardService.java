package com.assignment.rewardmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.assignment.rewardmanagement.dto.response.RewardResponse;

public interface RewardService {

    RewardResponse getRewardPointsForCustomer(Integer customerId);

    RewardResponse getRewardPointsForCustomerInRange(Integer customerId, LocalDateTime start, LocalDateTime end);

    List<RewardResponse> getAllCustomerRewards();
}
