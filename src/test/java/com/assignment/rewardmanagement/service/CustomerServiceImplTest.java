package com.assignment.rewardmanagement.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.rewardmanagement.dto.request.CustomerRequest;
import com.assignment.rewardmanagement.dto.response.CustomerResponse;
import com.assignment.rewardmanagement.entity.Customer;
import com.assignment.rewardmanagement.exception.DuplicateResourceException;
import com.assignment.rewardmanagement.exception.ResourceNotFoundException;
import com.assignment.rewardmanagement.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequest request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1);
        customer.setName("Clark");
        customer.setEmail("clark@gmail.com");

        request = new CustomerRequest();
        request.setName("Clark");
        request.setEmail("clark@gmail.com");
    }

    @Test
    void createCustomer_success() {
        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals("Clark", response.getName());
        assertEquals("clark@gmail.com", response.getEmail());
    }

    @Test
    void createCustomer_duplicateEmail_throwsException() {
        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> customerService.createCustomer(request));

        verify(customerRepository, never()).save(any()); // save should never be called if email already exists
    }

    @Test
    void getCustomerById_success() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomerById(1);

        assertEquals(1, response.getId());
        assertEquals("Clark", response.getName());
    }

    @Test
    void getCustomerById_notFound() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(99));
    }

    @Test
    void getAllCustomers_returnsAllCustomers() {
        Customer second = new Customer();
        second.setId(2);
        second.setName("Harry");
        second.setEmail("harry@gmail.com");

        when(customerRepository.findAll()).thenReturn(List.of(customer, second));

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
    }

    @Test
    void deleteCustomer_success() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1);

        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void deleteCustomer_notFound_throwsException() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(99));

        verify(customerRepository, never()).delete(any());
    }
}
