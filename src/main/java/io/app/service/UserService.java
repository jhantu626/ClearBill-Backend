package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;

public interface UserService {
    public UserDto profile(String authToken);
    public ApiResponse generateOtp(String token,String email);
    public ApiResponse addUser(String token,UserDto userDto);
}
