package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.exception.ResourceNotFoundException;
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
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepositoty repository;
    private final InvoiceItemsRepository itemsRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    public ApiResponse createInvoice(String token, List<InvoiceItem> items, Customer customer) {
        Optional<Customer> optionalCustomer=customerRepository.findByMobile(customer.getMobile());
        Customer customer1=null;
        if (optionalCustomer.isPresent()){
            customer1=optionalCustomer.get();
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
            double discount=item.getPrice()*(item.getDiscount()/100);
            totalDiscount+=discount;
            totalSubAmount+=item.getPrice();
            totalGst+=(item.getPrice()-discount)*item.getTotalGst()/100;
        }

        Invoice invoice=Invoice.builder()
                .items(savedItems)
                .subTotalAmount(totalSubAmount)
                .totalDiscount(totalDiscount)
                .totalGst(totalGst)
                .totalAmount(totalSubAmount-totalDiscount+totalGst)
                .customerName(customer.getName())
                .customerMobile(customer.getMobile())
                .business(business)
                .build();


        return ApiResponse.builder()
                .message("Created Invoice Successfully")
                .status(true)
                .build();
    }


    private String extractToken(String token){
        return jwtService.extractUsername(token);
    }
}
