package com.assignment.rewardmanagement.service;

import java.util.List;

import com.assignment.rewardmanagement.dto.request.CustomerRequest;
import com.assignment.rewardmanagement.dto.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Integer customerId);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(Integer customerId, CustomerRequest request);

    void deleteCustomer(Integer customerId);
}
