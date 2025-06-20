package io.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.app.dto.ApiResponse;
import io.app.dto.BusinessDto;
import io.app.service.impl.BusinessServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/business")
@RequiredArgsConstructor
@Slf4j
public class BusinessController {
    private final BusinessServiceImpl service;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createBusiness(@RequestParam("businessDetails") String businessDetails,
                                                      @RequestParam("logo") MultipartFile logo,
                                                      @RequestHeader("Authorization") String token) throws IOException {
        log.info("API - [Create Business] AT [{}]", LocalDateTime.now());
        ObjectMapper objectMapper=new ObjectMapper();
        BusinessDto businessDto=objectMapper.readValue(businessDetails,BusinessDto.class);
        return new ResponseEntity<>(service.createBusiness(businessDto,logo,token), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<BusinessDto> getBusiness(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(service.getBusiness(token));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse> updateBusiness(@RequestParam("business") String businessDetails,
                                                      @RequestParam(value = "logo",required = false) MultipartFile image) throws IOException {
        log.info("Update Business API - At [{}]",LocalDateTime.now());
        ObjectMapper objectMapper=new ObjectMapper();
        BusinessDto businessDto=objectMapper.readValue(businessDetails,BusinessDto.class);
        return ResponseEntity.ok(service.updateBusiness(businessDto,image));
    }
}
