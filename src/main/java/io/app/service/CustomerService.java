package io.app.service;

import io.app.model.Customer;

public interface CustomerService {
    public Customer getCustomerByMobile(String mobile);
}
