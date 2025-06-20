package io.app.dto;

import io.app.model.Business;
import io.app.model.Role;
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
public class UserDto {
    private long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private BusinessDto business;
}
