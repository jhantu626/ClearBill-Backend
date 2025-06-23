package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto profile(String authToken);
    public ApiResponse generateOtp(String token,String email);
    public ApiResponse generateOtp(String email);
    public ApiResponse addUser(String token,UserDto userDto);
    public List<UserDto> getAllUserOfBusiness(String token);
    public ApiResponse updateUser(UserDto userDto);
}
