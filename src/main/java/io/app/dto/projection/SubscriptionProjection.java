package io.app.dto.projection;

import io.app.model.SubscriptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionProjection(long id,
                                     SubscriptionType subscriptionType,
                                     LocalDateTime purchaseDate,
                                     LocalDateTime expirationDate) {
}