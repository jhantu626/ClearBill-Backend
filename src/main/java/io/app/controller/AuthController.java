package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;
import io.app.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl service;

    @PostMapping("/login/{email}")
    public ApiResponse login(@PathVariable("email") String email){
        return service.login(email);
    }

    @GetMapping("/verify-otp")
    public AuthResponse verifyOtp(@RequestParam("email") String email,
                                  @RequestParam("otp") String otp){
        log.info("Information is {} : {}",email,otp);
        return service.validateOtp(email,otp);
    }

}
