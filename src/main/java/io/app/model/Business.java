package io.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(length = 15)
    private String gstNo;
    private String address;
    private int stateCode;
    private String logo;
    @OneToMany(mappedBy = "business")
    private List<User> users=new ArrayList<>();
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    @PrePersist
    private void preCreate(){
        createdAt=LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        updatedAt=LocalDateTime.now();
    }

}
