package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(length = 50)
    private String description;
    private boolean isTaxable;
    @Column(length = 6)
    private String hsnCode;
    @Enumerated(EnumType.STRING)
    private UnitType unitType;
    private double price;
    private int cGst;
    private int sGst;
    private int iGst;
    private int discount;
    private String logo;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "business_id"))
    private Business business;


    @PrePersist
    private void preCreate(){
        this.createdAt=LocalDateTime.now();
        this.updateAt=LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        this.updateAt=LocalDateTime.now();
    }
}
