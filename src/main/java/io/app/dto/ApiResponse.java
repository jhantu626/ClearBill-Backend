package io.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiResponse {
    private String message;
    private boolean status;
}
