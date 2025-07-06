package io.app.service.impl;

import io.app.dto.DataLabel;
import io.app.dto.SalesOverViewType;
import io.app.exception.ResourceNotFoundException;
import io.app.model.Business;
import io.app.model.Invoice;
import io.app.model.InvoiceItem;
import io.app.repository.BusinessRepository;
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
    private final BusinessRepository businessRepository;

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

    public Map<String, Object> getBusinessReportForMonth(String token) {
        Map<String, Object> report = new HashMap<>();

        Business business = businessRepository
                .findByUsers(userRepository
                        .findByEmail(extractUsername(token))
                        .orElseThrow(()->new ResourceNotFoundException("Invalid Request")));

        // Get last 30 days data
        LocalDateTime endOfPeriod = LocalDateTime.now();
        LocalDateTime startOfPeriod = endOfPeriod.minusDays(30);

        // Fetch invoices within the last 30 days
        List<Invoice> invoices = repositoty.findByBusinessAndCreatedAtBetween(
                business,
                startOfPeriod,
                endOfPeriod
        );

        // Add business info
        report.put("businessName", business.getName());
        report.put("gstNumber", business.getGstNo());
        report.put("address", business.getAddress());
        report.put("stateCode", business.getStateCode());
        report.put("logo", business.getLogo());
        report.put("reportMonth", String.format("Last 30 Days (%02d %s %d to %02d %s %d)",
                startOfPeriod.getDayOfMonth(),
                startOfPeriod.getMonth().name().substring(0, 3).charAt(0) + startOfPeriod.getMonth().name().substring(1, 3).toLowerCase(),
                startOfPeriod.getYear(),
                endOfPeriod.getDayOfMonth(),
                endOfPeriod.getMonth().name().substring(0, 3).charAt(0) + endOfPeriod.getMonth().name().substring(1, 3).toLowerCase(),
                endOfPeriod.getYear()));
        report.put("createdAt", business.getCreatedAt());
        report.put("updatedAt", business.getUpdatedAt());

        // Aggregate data
        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalGst = invoices.stream().mapToDouble(Invoice::getTotalGst).sum();
        double totalDiscount = invoices.stream().mapToDouble(Invoice::getTotalDiscount).sum();
        int invoiceCount = invoices.size();

        Set<String> uniqueCustomers = invoices.stream()
                .map(Invoice::getCustomerMobile)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        report.put("totalRevenue", totalRevenue);
        report.put("totalGstCollected", totalGst);
        report.put("totalDiscount", totalDiscount);
        report.put("invoiceCount", invoiceCount);
        report.put("uniqueCustomerCount", uniqueCustomers.size());

        // Item analysis - Fix the relationship mapping
        Map<String, Integer> itemQuantityMap = new HashMap<>();
        Map<String, Double> itemRevenueMap = new HashMap<>();

        for (Invoice invoice : invoices) {
            // Check if items list is not null and not empty
            if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
                for (InvoiceItem item : invoice.getItems()) {
                    itemQuantityMap.merge(item.getName(), item.getQuantity(), Integer::sum);
                    double itemTotal = (item.getPrice() * item.getQuantity()) - item.getDiscount() + item.getTotalGst();
                    itemRevenueMap.merge(item.getName(), itemTotal, Double::sum);
                }
            }
        }

        List<Map<String, Object>> topItems = itemQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("itemName", e.getKey());
                    itemMap.put("quantitySold", e.getValue());
                    itemMap.put("totalRevenue", itemRevenueMap.getOrDefault(e.getKey(), 0.0));
                    return itemMap;
                })
                .collect(Collectors.toList());

        report.put("topItems", topItems);

        return report;
    }

    private String extractUsername(String token){
        return jwtService.extractUsername(token.substring(7));
    }
}
