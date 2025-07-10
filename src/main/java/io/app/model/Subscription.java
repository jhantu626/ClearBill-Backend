package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime purchaseDate;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id",nullable = false)
    private Business business;

    @Transient
    private boolean isActive(){
        return expirationDate!=null
                && expirationDate.isAfter(LocalDateTime.now());
    }
}
