package io.app.service;

import io.app.dto.ApiResponse;
import io.app.model.Customer;
import io.app.model.InvoiceItem;

import java.util.List;

public interface InvoiceService {
    ApiResponse createInvoice(String token, List<InvoiceItem> items, Customer customer);
}
