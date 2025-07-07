package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.BusinessDto;
import io.app.dto.UserDto;
import io.app.exception.ResourceNotFoundException;
import io.app.exception.UnAuthrizeException;
import io.app.model.Business;
import io.app.model.Role;
import io.app.model.User;
import io.app.repository.UserRepository;
import io.app.service.JwtService;
import io.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final MailServiceImpl mailService;

    @Override
    public UserDto profile(String authToken) {
        authToken=authToken.substring(7);
        String email=jwtService.extractUsername(authToken);
        User user=repository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Credentials"));
        UserDto userDto=UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        if (user.getBusiness()!=null){
            BusinessDto businessDto=new BusinessDto();
            businessDto.setId(user.getBusiness().getId());
            businessDto.setName(user.getBusiness().getName());
            businessDto.setGstNo(user.getBusiness().getGstNo());
            businessDto.setLogo(user.getBusiness().getLogo());
            businessDto.setAddress(user.getBusiness().getAddress());
            businessDto.setStateCode(user.getBusiness().getStateCode());
            businessDto.setCreatedAt(user.getBusiness().getCreatedAt());
            businessDto.setUpdatedAt(user.getBusiness().getUpdatedAt());
            userDto.setBusiness(businessDto);
        }

        return userDto;
    }

    @Override
    public ApiResponse generateOtp(String token, String email) {
        User user=repository.findByEmail(extractEmail(token))
                .orElseThrow(()->new ResourceNotFoundException("Invalid Credentials"));
        if (user.getEmail().equals(email.trim())){
            throw new UnAuthrizeException("You Can't add yourself as a Staff");
        }
        if (user.getRole()!=Role.ADMIN){
            throw new UnAuthrizeException("UnAuthorize Request");
        }
        if (user.getBusiness()==null){
            throw new UnAuthrizeException("Business Not found");
        }
        String otp="";
        Random random=new Random();
        for (int i=0;i<4;i++){
            otp+=random.nextInt(10);
        }
        String mailBody="Hi,\n" +
                "\n" +
                "You’ve been invited to join ClearBill for the business .\n" +
                "\n" +
                "To complete the setup and confirm your identity, please use the following One-Time Password (OTP):\n" +
                "\n" +
                "\uD83D\uDD10 OTP: ["+otp+"]\n" +
                "\n" +
                "This OTP is valid for the next 10 minutes.\n" +
                "If you were not expecting this request or believe it was sent in error, please ignore this email.\n" +
                "\n" +
                "Thank you,\n" +
                "The ClearBill Team";
        mailService.sendMail(email,"You're Being Added to ClearBill – Your OTP is "+otp,mailBody);
        return ApiResponse.builder()
                .message(otp)
                .status(true)
                .build();
    }


    @Override
    public ApiResponse generateOtp(String email) {
        String otp="";
        Random random=new Random();
        for (int i=0;i<4;i++){
            otp+=random.nextInt(10);
        }
        String mailBody="Hi,\n" +
                "\n" +
                "You’ve been invited to join ClearBill for the business .\n" +
                "\n" +
                "To complete the setup and confirm your identity, please use the following One-Time Password (OTP):\n" +
                "\n" +
                "\uD83D\uDD10 OTP: ["+otp+"]\n" +
                "\n" +
                "This OTP is valid for the next 10 minutes.\n" +
                "If you were not expecting this request or believe it was sent in error, please ignore this email.\n" +
                "\n" +
                "Thank you,\n" +
                "The ClearBill Team";
        mailService.sendMail(email,"You're Being Added to ClearBill – Your OTP is "+otp,mailBody);
        return ApiResponse.builder()
                .message(otp)
                .status(true)
                .build();
    }

    @Override
    public ApiResponse addUser(String token, UserDto userDto) {
        token=token.substring(7);
        String email=jwtService.extractUsername(token);
        User user=repository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Credentials"));
        if (user.getRole()!= Role.ADMIN){
            throw new UnAuthrizeException("You don't have access to create");
        }
        boolean isUserExist=repository.existsByEmail(userDto.getEmail().trim());
        User newUser=new User();
        if (isUserExist){
            User dbUser=repository.findByEmail(userDto.getEmail()).get();
            newUser.setId(dbUser.getId());
        }
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setPhone(userDto.getPhone());
        newUser.setRole(Role.STAFF);
        newUser.setBusiness(user.getBusiness());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        repository.save(newUser);
        return ApiResponse.builder()
                .message("Successfully Added User")
                .status(true)
                .build();
    }

    @Override
    public List<UserDto> getAllUserOfBusiness(String token) {
        User user=repository.findByEmail(extractEmail(token))
                .orElseThrow(()->new ResourceNotFoundException("Invalid User"));
        if (user.getBusiness()==null){
            return new ArrayList<>();
        }
        List<UserDto> result=repository.findByBusiness(user.getBusiness())
                .stream().parallel().map((userDto)->{
                    UserDto dto=new UserDto();
                    dto.setId(userDto.getId());
                    dto.setName(userDto.getName());
                    dto.setPhone(userDto.getPhone());
                    dto.setEmail(userDto.getEmail());
                    dto.setRole(userDto.getRole());
                    dto.setCreatedAt(userDto.getCreatedAt());
                    dto.setUpdatedAt(userDto.getUpdatedAt());
                    return dto;
                }).collect(Collectors.toList());
        return result;
    }

    @Override
    public ApiResponse updateUser(UserDto userDto) {
        User user=repository.findByEmail(userDto.getEmail())
                .orElseThrow(()->new ResourceNotFoundException("UnAuthorize Access"));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        repository.save(user);
        return ApiResponse.builder()
                .status(true)
                .message("Updated Successfully")
                .build();
    }

    @Override
    public ApiResponse removeBusiness(String token,long userID) {
        Role role=repository.findRoleByEmail(extractEmail(token));
        if (role==null){
            throw new ResourceNotFoundException("Invalid Request");
        }
        if (role.name()!="ADMIN"){
            throw new UnAuthrizeException("Only admin can remove and add user");
        }
        User user=repository.findById(userID)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        if (user.getRole().name()=="ADMIN"){
            throw new UnAuthrizeException("Sorry `ADMIN` can't remove himself");
        }

        user.setBusiness(null);
        user.setRole(Role.ADMIN);
        repository.save(user);
        return ApiResponse.builder()
                .status(true)
                .message("Removed Successfully")
                .build();
    }


    private String extractEmail(String token){
        token=token.substring(7);
        return jwtService.extractUsername(token);
    }


}