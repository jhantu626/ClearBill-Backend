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
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private boolean isTaxable;
    private String hsnCode;
    private UnitType unitType;
    private double price;
    private String gstType;
    private double totalGst;
    private double discount;
    private int quantity;
    private String logo;
}
