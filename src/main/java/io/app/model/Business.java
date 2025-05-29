package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String gstNo;
    private String address;
    private int stateCode;
    private String logo;
    @OneToMany(mappedBy = "business" )
    private List<User> users=new ArrayList<>();
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
