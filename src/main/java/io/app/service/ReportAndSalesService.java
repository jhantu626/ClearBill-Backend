package io.app.service;

import io.app.dto.SalesOverViewType;

import java.util.Map;

public interface ReportAndSalesService {
    public Map<String, Object> generateSalesOverview(String token,SalesOverViewType type);
}
