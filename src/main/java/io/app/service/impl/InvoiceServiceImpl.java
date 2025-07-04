package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.InvoiceDto;
import io.app.exception.ResourceNotFoundException;
import io.app.helper.Helper;
import io.app.model.Business;
import io.app.model.Customer;
import io.app.model.Invoice;
import io.app.model.InvoiceItem;
import io.app.repository.CustomerRepository;
import io.app.repository.InvoiceItemsRepository;
import io.app.repository.InvoiceRepositoty;
import io.app.repository.UserRepository;
import io.app.service.InvoiceService;
import io.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepositoty repository;
    private final InvoiceItemsRepository itemsRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Helper helper;


    @Override
    public InvoiceDto createInvoice(String token, List<InvoiceItem> items,
                                    Customer customer) {
        Optional<Customer> optionalCustomer=customerRepository.findByMobile(customer.getMobile());
        Customer customer1=null;
        if (optionalCustomer.isPresent()){
            customer1=optionalCustomer.get();
            if (!customer1.getName().equals(customer.getName())){
                customer1.setName(customer.getName());
                customer1=customerRepository.save(customer1);
            }
        }else {
            customer1=Customer.builder()
                    .name(customer.getName())
                    .mobile(customer.getMobile())
                    .build();
            customer1=customerRepository.save(customer1);
        }

        Business business=userRepository.findBusinessByEmail(extractToken(token))
                .orElseThrow(()->new ResourceNotFoundException("Business Not Found!"));

        double totalSubAmount=0d;
        double totalGst=0d;
        double totalDiscount=0d;

        List<InvoiceItem> savedItems=itemsRepository.saveAll(items);

        for (InvoiceItem item:savedItems){
            double discount=(item.getPrice()*item.getQuantity())*(item.getDiscount()/100);
            totalDiscount+=discount;
            totalSubAmount+=item.getPrice()*item.getQuantity();
            totalGst+=((item.getPrice()*item.getQuantity())-discount)*item.getTotalGst()/100;
        }

        Invoice saveInvoice=Invoice.builder()
                .items(savedItems)
                .subTotalAmount(totalSubAmount)
                .totalDiscount(totalDiscount)
                .totalGst(totalGst)
                .totalAmount(totalSubAmount-totalDiscount+totalGst)
                .customerName(customer.getName())
                .customerMobile(customer.getMobile())
                .business(business)
                .build();

        Invoice invoice=repository.save(saveInvoice);
        InvoiceDto result=InvoiceDto.builder()
                .id(invoice.getId())
                .business(helper.businessToDto(invoice.getBusiness()))
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .customerGstNo(invoice.getCustomerGstNo())
                .customerMobile(invoice.getCustomerMobile())
                .customerName(invoice.getCustomerName())
                .items(invoice.getItems())
                .name(invoice.getName())
                .subTotalAmount(invoice.getSubTotalAmount())
                .totalAmount(invoice.getTotalAmount())
                .totalDiscount(invoice.getTotalDiscount())
                .totalGst(invoice.getTotalGst())
                .build();
        return result;
    }

    @Override
    public List<InvoiceDto> getInvoice(String token, int page, int size) {
        Optional<Long> businessId=userRepository.findBusinessIdByEmail(extractToken(token));
        if (!businessId.isPresent()){
            return List.of();
        }

        Pageable pageable=PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Invoice> invoices= repository.findByBusiness(Business.builder()
        .id(businessId.get()).build(), pageable);
        List<InvoiceDto> result=invoices.getContent().stream().map((invoice)->{
            InvoiceDto invoiceDto=InvoiceDto.builder()
                    .id(invoice.getId())
                    .business(helper.businessToDto(invoice.getBusiness()))
                    .createdAt(invoice.getCreatedAt())
                    .updatedAt(invoice.getUpdatedAt())
                    .customerGstNo(invoice.getCustomerGstNo())
                    .customerMobile(invoice.getCustomerMobile())
                    .customerName(invoice.getCustomerName())
                    .items(invoice.getItems())
                    .name(invoice.getName())
                    .subTotalAmount(invoice.getSubTotalAmount())
                    .totalAmount(invoice.getTotalAmount())
                    .totalDiscount(invoice.getTotalDiscount())
                    .totalGst(invoice.getTotalGst())
                    .build();;
            return invoiceDto;
        }).collect(Collectors.toList());


        return result;
    }


    private String extractToken(String token){
        return jwtService.extractUsername(token.substring(7));
    }
}
