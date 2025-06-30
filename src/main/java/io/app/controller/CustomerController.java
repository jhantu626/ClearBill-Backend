package io.app.controller;

import io.app.model.Customer;
import io.app.service.impl.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerServiceImpl service;

    @GetMapping
    public Customer customer(@RequestParam("mobile") String mobile){
        return service.getCustomerByMobile(mobile);
    }
}
