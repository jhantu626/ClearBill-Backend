package io.app.controller;

import io.app.dto.ApiResponse;
import io.app.dto.UserDto;
import io.app.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl service;


    @GetMapping
    public ResponseEntity<UserDto> profile(@RequestHeader("Authorization") String token){

        return ResponseEntity.ok(service.profile(token));
    }


    @PostMapping("/add-user/{email}")
    public ResponseEntity<ApiResponse> createUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("email") String email){
        return ResponseEntity.ok(service.generateOtp(token,email));
    }

    @PostMapping("/add-user/verify")
    public ResponseEntity<ApiResponse> createUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDto user){
        return new ResponseEntity<>(service.addUser(token,user), HttpStatus.CREATED);
    }

    @GetMapping("/business")
    public ResponseEntity<List<UserDto>> usersByBusiness(
            @RequestHeader("Authorization") String token
    ){
        return ResponseEntity.ok(service.getAllUserOfBusiness(token));
    }

    /*
    * Update User Api
    * Endpoint: /api/v1/user
    * Method: PUT
    */
    @PutMapping
    public ResponseEntity<ApiResponse> updateUSer(@RequestBody UserDto userDto){
        return ResponseEntity.ok(service.updateUser(userDto));
    }

    @GetMapping("update-user/verify/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse verifyOtp(@PathVariable("email") String email){
        return service.generateOtp(email);
    }

    @PutMapping("/business/remove-business/{id}")
    public ResponseEntity<ApiResponse> removeBusiness(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") long userId){
        return ResponseEntity.ok(service.removeBusiness(token,userId));
    }

}
