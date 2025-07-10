package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double price;
    private String razorpayPaymentId;
    private String userEmail;
    private String userMobile;
    private String userName;
    @OneToOne(optional = false)
    @JoinColumn(name = "subscription_id",nullable = false)
    private Subscription subscription;
}
