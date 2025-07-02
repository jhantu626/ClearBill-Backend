package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.InvoiceDto;
import io.app.model.Customer;
import io.app.model.Invoice;
import io.app.model.InvoiceItem;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(String token, List<InvoiceItem> items, Customer customer);
    List<InvoiceDto> getInvoice(String token,int page,int size);
}
