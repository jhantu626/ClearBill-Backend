package io.app.service.impl;

import io.app.dto.DataLabel;
import io.app.dto.SalesOverViewType;
import io.app.model.Business;
import io.app.model.Invoice;
import io.app.repository.InvoiceRepositoty;
import io.app.repository.UserRepository;
import io.app.service.JwtService;
import io.app.service.ReportAndSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportAndSalesServiceImpl implements ReportAndSalesService {
    private final InvoiceRepositoty repositoty;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public Map<String, Object> generateSalesOverview(String token, SalesOverViewType type) {
        Optional<Long> businessId =userRepository.findBusinessIdByEmail(extractUsername(token));
        if (!businessId.isPresent()){
            return Map.of();
        }
        Long business=businessId.isPresent()?businessId.get():0;
        double totalSales=0;
        double previousSum= 0l;
        Business searchBusiness= Business.builder().id(business).build();
        LocalDateTime start=LocalDate.now().atStartOfDay();
        LocalDateTime end=LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime previousStart=LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime previousEnd=LocalDate.now().atStartOfDay();


        // COMMON DATA
        Map<?,Double> data=new HashMap<>();

        if (type.name().equals("DAY")){
            List<Invoice> invoices=repositoty.findByBusinessAndCreatedAtBetween(
                    searchBusiness,
                    start,
                    end
            );
            data=invoices.stream().collect(
                    Collectors.groupingBy(
                            invoice -> invoice.getCreatedAt().getHour(),
                            Collectors.summingDouble(Invoice::getTotalAmount)
                    )
            );
        }
        else if (type.name().equals("WEEK")){
            start=LocalDate.now()
                    .minusDays(7)
                    .atStartOfDay();
            previousStart=LocalDate.now()
                    .minusDays(14)
                    .atStartOfDay();
            previousEnd=LocalDate.now()
                    .minusDays(7)
                    .atStartOfDay();
            List<Invoice> invoices=repositoty.findByBusinessAndCreatedAtBetween(
                    searchBusiness,
                    start,
                    end
            );
            data=invoices.stream().collect(
                    Collectors.groupingBy(
                            invoice -> invoice.getCreatedAt().getDayOfWeek().toString(),
                            Collectors.summingDouble(Invoice::getTotalAmount)
                    )
            );
        }else if (type.name().equals("MONTH")){
            start=LocalDate.now()
                    .minusDays(30)
                    .atStartOfDay();
            previousStart=LocalDate.now()
                    .minusDays(60)
                    .atStartOfDay();
            previousEnd=LocalDate.now()
                    .minusDays(30)
                    .atStartOfDay();
            List<Invoice> invoices=repositoty.findByBusinessAndCreatedAtBetween(
                    searchBusiness,
                    start,
                    end
            );
            data=invoices.stream().collect(
                    Collectors.groupingBy(
                            invoice -> invoice.getCreatedAt().getDayOfMonth(),
                            Collectors.summingDouble(Invoice::getTotalAmount)
                    )
            );
        }



        previousSum=repositoty.findByBusinessAndCreatedAtBetween(
                searchBusiness,
                previousStart,
                previousEnd
        ).stream().collect(Collectors.summingDouble(Invoice::getTotalAmount));


        List<DataLabel> dataSet=new ArrayList<>();
        for (Map.Entry<?,Double> entry:data.entrySet()){
            dataSet.add(DataLabel.builder()
                    .label(entry.getKey().toString())
                    .value(entry.getValue())
                    .build());
            totalSales+=entry.getValue();
        }
        double percentage;
        if (previousSum == 0) {
            percentage = 0;
        } else {
            percentage = ((totalSales - previousSum) / previousSum) * 100;
        }
        Map<String,Object> result=new HashMap<>();
        result.put("dataSet",dataSet);
        result.put("percentage",percentage);
        result.put("totalSum",totalSales);
        return result;
    }

    private String extractUsername(String token){
        return jwtService.extractUsername(token.substring(7));
    }
}
