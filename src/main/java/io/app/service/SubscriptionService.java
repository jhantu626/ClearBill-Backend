package io.app.service;


import io.app.dto.ApiResponse;
import io.app.dto.projection.SubscriptionProjection;
import io.app.model.SubscriptionType;
import io.app.model.Transaction;

public interface SubscriptionService {
    public ApiResponse createSubscription(String token,SubscriptionType type, Transaction transaction);
    public SubscriptionProjection getCurrentSubscription(String authToken);
}
