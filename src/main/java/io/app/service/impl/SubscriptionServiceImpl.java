package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.projection.SubscriptionProjection;
import io.app.exception.ResourceNotFoundException;
import io.app.exception.UnAuthrizeException;
import io.app.model.Subscription;
import io.app.model.SubscriptionType;
import io.app.model.Transaction;
import io.app.model.User;
import io.app.repository.BusinessRepository;
import io.app.repository.SubscriptionRepository;
import io.app.repository.TransactionRepository;
import io.app.repository.UserRepository;
import io.app.service.JwtService;
import io.app.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final JwtService jwtService;
    private final TransactionRepository transactionRepository;

    @Override
    public ApiResponse createSubscription(String token,
                                          SubscriptionType type,
                                          Transaction transaction) {
        User user=userRepository.findByEmail(extractUserName(token))
                .orElseThrow(()->new UnAuthrizeException("UnAuthorize Request"));
        if (user.getBusiness()==null || !businessRepository.existsById(user.getBusiness().getId())){
            throw new UnAuthrizeException("Sorry No Business Found!");
        }

        Subscription subscription=Subscription.builder()
                .subscriptionType(type)
                .purchaseDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(31))
                .business(user.getBusiness())
                .build();

        Subscription savedSubscription=repository.save(subscription);
        transaction.setSubscription(subscription);
        transaction.setUserName(user.getName());
        transaction.setUserMobile(user.getPhone());
        transaction.setUserEmail(user.getEmail());
        transactionRepository.save(transaction);

        return ApiResponse.builder()
                .message("Purchased Successfully!")
                .status(true)
                .build();
    }

    @Override
    public SubscriptionProjection getCurrentSubscription(String authToken) {
        long businessId=userRepository.findBusinessIdByEmail(extractUserName(authToken))
                .orElseThrow(()->new ResourceNotFoundException("Business Not found"));
        SubscriptionProjection subscriptionProjection=repository.findCurrentSubscriptionByBusiness(businessId,LocalDateTime.now());
        return subscriptionProjection;
    }

    private String extractUserName(String token){
        return jwtService.extractUsername(token.substring(7));
    }
}
