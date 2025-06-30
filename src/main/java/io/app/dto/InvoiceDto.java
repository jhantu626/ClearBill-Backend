package io.app.dto;

import io.app.model.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvoiceDto {
    private long id;
    private String name;
    private double subTotalAmount;
    private double totalAmount;
    private double totalDiscount;
    private double totalGst;
    private String customerName;
    private String customerMobile;
    private String customerGstNo;
    private BusinessDto business;
    private List<InvoiceItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
