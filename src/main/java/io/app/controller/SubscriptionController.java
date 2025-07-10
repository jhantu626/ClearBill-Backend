package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.model.SubscriptionType;
import io.app.model.Transaction;
import io.app.service.impl.SubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionServiceImpl service;

    @PostMapping
    public ResponseEntity<ApiResponse> createSubscription(
            @RequestHeader("Authorization") String token,
            @RequestParam("type") SubscriptionType type,
            @RequestBody Transaction transaction
    ){
        return new ResponseEntity<>(service.createSubscription(token,type,transaction), HttpStatus.CREATED);
    }
}
