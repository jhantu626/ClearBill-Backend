package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name="INV";
    private double subTotalAmount;
    private double totalAmount;
    private double totalDiscount;
    private double totalGst;
    private String customerName;
    private String customerMobile;
    @ManyToOne
    @JoinColumn(name = "businessId")
    private Business business;
    @OneToMany
    private List<InvoiceItem> items;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    @PrePersist
    public void preCreate(){
            this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt= LocalDateTime.now();
    }


}
