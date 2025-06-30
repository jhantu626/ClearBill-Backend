package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.model.Customer;
import io.app.model.InvoiceItem;
import io.app.service.InvoiceService;
import io.app.service.impl.InvoiceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceServiceImpl service;


    @PostMapping
    public ApiResponse createInvoice(@RequestHeader("Authorization") String token,
                                     @RequestBody List<InvoiceItem> items,
                                     @RequestBody Customer customer){
        return service.createInvoice(token,items,customer);
    }
}
