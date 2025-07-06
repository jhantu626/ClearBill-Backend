package io.app.controller;

import io.app.dto.SalesOverViewType;
import io.app.service.impl.ReportAndSalesServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales-overview")
@RequiredArgsConstructor
public class ReportAndSalesController {
    private final ReportAndSalesServiceImpl service;

    @GetMapping
    public Map<String,Object> getOverviewOfSales(
            @RequestHeader("Authorization") String token,
            @RequestParam SalesOverViewType type){
        return service.generateSalesOverview(token,type);
    }

    @GetMapping("/report")
    public Map<?,?> businessReport(
            @RequestHeader("Authorization") String token){
        return service.getBusinessReportForMonth(token);
    }
}
