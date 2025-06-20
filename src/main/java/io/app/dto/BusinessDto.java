package io.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BusinessDto {
    private long id;
    private String name;
    private String gstNo;
    private String address;
    private int stateCode;
    private String logo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}