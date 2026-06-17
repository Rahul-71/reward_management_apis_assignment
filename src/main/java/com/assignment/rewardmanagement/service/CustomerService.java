package com.assignment.rewardmanagement.service;

import com.assignment.rewardmanagement.dto.request.CustomerRequest;
import com.assignment.rewardmanagement.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Integer customerId);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(Integer customerId, CustomerRequest request);

    void deleteCustomer(Integer customerId);
}
