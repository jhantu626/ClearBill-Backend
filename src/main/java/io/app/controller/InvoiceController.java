package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.InvoiceDto;
import io.app.model.Customer;
import io.app.model.Invoice;
import io.app.model.InvoiceItem;
import io.app.service.InvoiceService;
import io.app.service.impl.InvoiceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceServiceImpl service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createInvoice(@RequestHeader("Authorization") String token,
                                    @RequestBody List<InvoiceItem> items,
                                    @RequestParam("customer-mobile") String customerNumber,
                                    @RequestParam("customer-name") String customerName){
        return service.createInvoice(token,items,Customer.builder()
                .mobile(customerNumber)
                .name(customerName).build());
    }


    /*
    * GET - INVOICE
    * URI - `/api/v1/invoice?page=5&size=10`
    * METHOD: [GET]
    */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InvoiceDto> getInvoice(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "page",required = false,defaultValue = "0") int page,
            @RequestParam(value = "size",required = false,defaultValue = "5") int size){
        return service.getInvoice(token,page,size);
    }



}
