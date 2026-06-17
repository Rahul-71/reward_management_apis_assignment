package com.assignment.rewardmanagement.dto.response;

import java.time.Month;
import java.util.Map;

public class RewardResponse {

    private Integer customerId;
    private String customerName;
    private Map<Month, Integer> monthlyRewardPoints;
    private int totalRewardPoints;

    public RewardResponse() {
    }

    public RewardResponse(Integer customerId, String customerName,
            Map<Month, Integer> monthlyRewardPoints, int totalRewardPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewardPoints = monthlyRewardPoints;
        this.totalRewardPoints = totalRewardPoints;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Map<Month, Integer> getMonthlyRewardPoints() {
        return monthlyRewardPoints;
    }

    public void setMonthlyRewardPoints(Map<Month, Integer> monthlyRewardPoints) {
        this.monthlyRewardPoints = monthlyRewardPoints;
    }

    public int getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(int totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }
}
