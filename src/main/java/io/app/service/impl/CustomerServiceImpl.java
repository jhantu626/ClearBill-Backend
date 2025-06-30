package io.app.service.impl;

import io.app.exception.ResourceNotFoundException;
import io.app.model.Customer;
import io.app.repository.CustomerRepository;
import io.app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;

    @Override
    public Customer getCustomerByMobile(String mobile) {
        Customer customer=repository.findByMobile(mobile)
                .orElseThrow(()->new ResourceNotFoundException("Not Found"));
        return customer;
    }
}
