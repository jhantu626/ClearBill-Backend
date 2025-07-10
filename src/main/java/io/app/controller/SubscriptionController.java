package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.projection.SubscriptionProjection;
import io.app.model.SubscriptionType;
import io.app.model.Transaction;
import io.app.service.impl.SubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionServiceImpl service;

    @PostMapping
    public ResponseEntity<ApiResponse> createSubscription(
            @RequestHeader("Authorization") String token,
            @RequestParam("type") SubscriptionType type,
            @RequestBody Transaction transaction
    ){
        log.info("Inside the Create Subscription [{}]", LocalDateTime.now());
        return new ResponseEntity<>(service.createSubscription(token,type,transaction), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<SubscriptionProjection>
        getCurrentSubscription(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(service.getCurrentSubscription(token));
    }
}
