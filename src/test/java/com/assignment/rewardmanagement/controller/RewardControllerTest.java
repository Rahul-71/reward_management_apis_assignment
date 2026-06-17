package com.assignment.rewardmanagement.controller;

import java.time.Month;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assignment.rewardmanagement.dto.response.RewardResponse;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.service.RewardService;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardService rewardService;

    @Test
    void getAllCustomerRewards_returns200() throws Exception {
        RewardResponse r1 = new RewardResponse(1, "Clark", Map.of(Month.APRIL, 90), 90);
        RewardResponse r2 = new RewardResponse(2, "Harry", Map.of(), 0);
        when(rewardService.getAllCustomerRewards()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRewardPoints_noDateParams_returns200() throws Exception {
        RewardResponse response = new RewardResponse(1, "Clark", Map.of(Month.APRIL, 90), 90);
        when(rewardService.getRewardPointsForCustomer(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalRewardPoints").value(90));
    }

    @Test
    void getRewardPoints_withDateRange_returns200() throws Exception {
        RewardResponse response = new RewardResponse(1, "Clark", Map.of(Month.APRIL, 90), 90);
        when(rewardService.getRewardPointsForCustomerInRange(eq(1), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/rewards/1")
                .param("startDate", "2026-04-01")
                .param("endDate", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRewardPoints").value(90));
    }

    @Test
    void getRewardPoints_onlyStartDate_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/1")
                .param("startDate", "2026-04-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRewardPoints_startAfterEnd_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/1")
                .param("startDate", "2026-05-01")
                .param("endDate", "2026-04-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRewardPoints_customerNotFound_returns404() throws Exception {
        when(rewardService.getRewardPointsForCustomer(99))
                .thenThrow(new ResourceNotFoundException("Customer", "id", 99));

        mockMvc.perform(get("/api/v1/rewards/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
