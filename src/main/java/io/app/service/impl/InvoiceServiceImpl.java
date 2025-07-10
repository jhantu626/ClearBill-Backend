package io.app.service.impl;

import io.app.dto.InvoiceDto;
import io.app.exception.ResourceNotFoundException;
import io.app.exception.UnAuthrizeException;
import io.app.helper.Helper;
import io.app.model.*;
import io.app.repository.*;
import io.app.service.InvoiceService;
import io.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepositoty repository;
    private final InvoiceItemsRepository itemsRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Helper helper;
    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;


    @Override
    public InvoiceDto createInvoice(String token, List<InvoiceItem> items,
                                    Customer customer) {
        Business business=userRepository.findBusinessByEmail(extractToken(token))
                .orElseThrow(()->new ResourceNotFoundException("Business Not Found!"));

        long invoiceCount = getCountOfInvoice(business.getId());
        Subscription subscription=subscriptionRepository.findByExpirationDateGreaterThanAndBusiness(
                LocalDateTime.now(),business.getId()
        );
        if (subscription==null){
            throw new UnAuthrizeException("Oops! Your free trial ended");
        }
        if ((subscription.getSubscriptionType() == SubscriptionType.STARTER &&
            invoiceCount>=1) || (subscription.getSubscriptionType() == SubscriptionType.PRO && invoiceCount>=2)){
            throw new UnAuthrizeException("Invoice limit exceeded");
        }

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

    @Override
    public long getCountOfInvoice(String token) {
        long businessId=userRepository
                .findBusinessIdByEmail(extractToken(token))
                .orElseThrow(()->new ResourceNotFoundException("Business Not Found!"));
        LocalDateTime start=LocalDateTime.now();
        LocalDateTime end=LocalDate.now().atStartOfDay();
        long count=repository.countByBusinessAndCreatedAt(businessId,start,end);
        return count;
    }

    @Override
    public long getCountOfInvoice(long id) {
        LocalDateTime start=LocalDateTime.now();
        LocalDateTime end=LocalDate.now().atStartOfDay();
        long count=repository.countByBusinessAndCreatedAt(id,start,end);
        log.info("Count is: {}",count);
        return count;
    }


    private String extractToken(String token){
        return jwtService.extractUsername(token.substring(7));
    }
}
