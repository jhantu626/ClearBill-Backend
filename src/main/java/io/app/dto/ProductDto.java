package io.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.app.model.UnitType;
import io.app.service.ProductService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private long id;
    private String name;
    private String description;
    private boolean isTaxable;
    private String hsnCode;
    private UnitType unitType;
    private double price;
    private int cGst;
    private int sGst;
    private int iGst;
    private int discount;
    private String logo;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public boolean getIsTaxable(){
        return this.isTaxable;
    }

    public void setIsTaxable(boolean isTaxable){
        this.isTaxable=isTaxable;
    }
}
