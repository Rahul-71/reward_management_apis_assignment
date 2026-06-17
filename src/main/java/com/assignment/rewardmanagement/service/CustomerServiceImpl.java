package com.assignment.rewardmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.assignment.rewardmanagement.dto.request.CustomerRequest;
import com.assignment.rewardmanagement.dto.response.CustomerResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer already exists with email: " + request.getEmail());
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        Customer saved = customerRepository.save(customer);
        log.info("Customer created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public CustomerResponse getCustomerById(Integer customerId) {
        log.info("Fetching customer with id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(Integer customerId, CustomerRequest request) {
        log.info("Updating customer with id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        Customer updated = customerRepository.save(customer);
        log.info("Customer updated with id: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        log.info("Deleting customer with id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        customerRepository.delete(customer);
        log.info("Customer deleted with id: {}", customerId);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
