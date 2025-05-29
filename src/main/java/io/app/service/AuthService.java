package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;

public interface AuthService {
    public ApiResponse login(String email);
    public AuthResponse validateOtp(String email, String otp);
}
