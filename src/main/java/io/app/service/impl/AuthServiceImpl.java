package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.AuthResponse;
import io.app.exception.ResourceNotFoundException;
import io.app.model.User;
import io.app.repository.UserRepository;
import io.app.service.AuthService;
import io.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final MailServiceImpl mailService;

    @Override
    public ApiResponse login(String email) {
        boolean isExist=repository.existsByEmail(email);
        String token="";
        if (isExist){
            String otp=generateOtp();
            String subject="Your One-Time Password (OTP) for Verification";
            String mailBody = "Hello,\n\n" +
                    "Your One-Time Password (OTP) for verification is:\n\n" +
                    "🔐 OTP: " + otp + "\n\n" +
                    "Please enter this code within the next 5 minutes to complete your action.\n" +
                    "If you did not request this code, please ignore this email.\n\n" +
                    "Thank you,\n ClearBill Support Team";
            mailService.sendMail(email,subject,mailBody);
            User user=repository.findByEmail(email)
                    .orElseThrow(()->new ResourceNotFoundException("Something went wrong"));
            user.setOtp(otp);
            user.setOtpExpiration(LocalDateTime.now().plusMinutes(2));
            repository.save(user);
        }else {
            String otp=generateOtp();
            String subject="Welcome to ClearBill – Verify Your Account with This OTP";
            String mailBody = "Hello,\n\n" +
                    "Your registration was successful!\n\n" +
                    "To complete the verification process, please use the following One-Time Password (OTP):\n\n" +
                    "🔐 OTP: " + otp + "\n\n" +
                    "Enter this code within the next 5 minutes to verify your account.\n" +
                    "If you did not create this account, please ignore this email.\n\n" +
                    "Thank you,\n" +
                    "ClearBill Support Team";
            mailService.sendMail(email,subject,mailBody);
            User user=User.builder()
                    .email(email)
                    .otp(otp)
                    .otpExpiration(LocalDateTime.now().plusMinutes(2))
                    .build();

            repository.save(user);
        }
        return ApiResponse.builder()
                .message("Otp generated successfully")
                .status(true)
                .build();
    }

    @Override
    public AuthResponse validateOtp(String email, String otp) {
        User user=repository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Details"));
        if (user.getOtp().equals(otp) && user.getOtpExpiration().isAfter(LocalDateTime.now())){
            String token=jwtService.generateToken(user);
            return AuthResponse.builder()
                    .token(token)
                    .status(true)
                    .build();
        }
        return AuthResponse.builder()
                .status(false)
                .build();
    }

    private String generateOtp(){
        String otp="";
        Random random=new Random();
        for (int i=0;i<4;i++){
            otp+=random.nextInt(1,9);
        }
        return otp;
    }
}
