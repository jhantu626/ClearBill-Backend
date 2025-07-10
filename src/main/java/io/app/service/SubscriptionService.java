package io.app.service;


import io.app.dto.ApiResponse;
import io.app.model.SubscriptionType;
import io.app.model.Transaction;

public interface SubscriptionService {
    public ApiResponse createSubscription(String token,SubscriptionType type, Transaction transaction);
}
