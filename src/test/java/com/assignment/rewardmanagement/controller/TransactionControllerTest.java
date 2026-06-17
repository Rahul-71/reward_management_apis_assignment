package com.assignment.rewardmanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assignment.rewardmanagement.dto.response.TransactionResponse;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.service.TransactionService;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void addTransaction_validRequest_returns201() throws Exception {
        TransactionResponse response = new TransactionResponse(
                101, 1, "Clark", new BigDecimal("120.00"), LocalDateTime.of(2026, 4, 5, 10, 0));
        when(transactionService.addTransaction(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "customerId": 1,
                            "amount": 120.00,
                            "transactionDate": "2026-04-05T10:00:00"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(101))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.amount").value(120.00));
    }

    @Test
    void addTransaction_missingCustomerId_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "amount": 120.00,
                            "transactionDate": "2026-04-05T10:00:00"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addTransaction_negativeAmount_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "customerId": 1,
                            "amount": -10.00,
                            "transactionDate": "2026-04-05T10:00:00"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getTransactionsByCustomer_returns200() throws Exception {
        List<TransactionResponse> transactions = List.of(
                new TransactionResponse(1, 1, "Clark", new BigDecimal("75.00"), LocalDateTime.of(2026, 4, 5, 10, 0)),
                new TransactionResponse(2, 1, "Clark", new BigDecimal("120.00"), LocalDateTime.of(2026, 5, 10, 9, 0))
        );
        when(transactionService.getTransactionsByCustomer(1)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTransactionsByCustomer_customerNotFound_returns404() throws Exception {
        when(transactionService.getTransactionsByCustomer(99))
                .thenThrow(new ResourceNotFoundException("Customer", "id", 99));

        mockMvc.perform(get("/api/v1/transactions/customer/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
