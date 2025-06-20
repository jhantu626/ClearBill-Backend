package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.BusinessDto;
import io.app.exception.DuplicateResourceException;
import io.app.exception.ResourceNotFoundException;
import io.app.exception.UnAuthrizeException;
import io.app.model.Business;
import io.app.model.Role;
import io.app.model.User;
import io.app.repository.BusinessRepository;
import io.app.repository.UserRepository;
import io.app.service.BusinessService;
import io.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BusinessRepository repository;

    @Value("${file.logo}")
    private String logoPath;

    @Override
    public ApiResponse createBusiness(BusinessDto businessDto,
                                      MultipartFile logo,
                                      String token) throws IOException {
        String email=jwtService.extractUsername(token.substring(7));
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("UnAuthorize Access"));
        if (user.getRole()!= Role.ADMIN){
            throw new UnAuthrizeException("User can't create or modify the business");
        }
        if (user.getBusiness()!=null){
            throw new DuplicateResourceException("Already you have a business");
        }
        System.out.println(user.getRole());
        String logoName=saveImage(logo);

        Business business=Business.builder()
                .name(businessDto.getName())
                .address(businessDto.getAddress())
                .gstNo(businessDto.getGstNo())
                .stateCode(businessDto.getStateCode())
                .logo(logoName)
                .build();
        Business savedBusiness=repository.save(business);
        user.setBusiness(savedBusiness);
        userRepository.save(user);
        return ApiResponse.builder()
                .status(true)
                .message("Business Added Successfully")
                .build();
    }

    @Override
    public BusinessDto getBusiness(String token) {
        String email=jwtService.extractUsername(token.substring(7));
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Invalid User Details"));
        System.out.println(user.getEmail());
        System.out.println(user.getId());
        Business business=user.getBusiness();
        if (business==null){
            throw new ResourceNotFoundException("No Business Found");
        }
        BusinessDto businessDto= BusinessDto.builder()
                .id(business.getId())
                .name(business.getName())
                .gstNo(business.getGstNo())
                .logo(business.getLogo())
                .address(business.getAddress())
                .stateCode(business.getStateCode())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();

        return businessDto;
    }

    @Override
    public ApiResponse updateBusiness(BusinessDto businessDto,
                                      MultipartFile image) throws IOException {
        Business business=repository.findById(businessDto.getId())
                .orElseThrow(()->new ResourceNotFoundException("Invalid Details"));
        business.setName(businessDto.getName());
        business.setGstNo(businessDto.getGstNo());
        business.setAddress(businessDto.getAddress());
        business.setStateCode(businessDto.getStateCode());
        if (image!=null){
            boolean deleted=deleteLogo(business.getLogo());
            if (deleted){
                String logoName=saveImage(image);
                business.setLogo(logoName);
            }
        }
        repository.save(business);
        return ApiResponse.builder()
                .status(true)
                .message("Updated Successfully")
                .build();
    }


    private String saveImage(MultipartFile file) throws IOException {
        File folder=new File(logoPath);
        if (!folder.exists()){
            folder.mkdirs();
        }
        byte[] fileByte=file.getBytes();
        String name=UUID.randomUUID().toString()+
                file.getOriginalFilename()
                        .substring(file.getOriginalFilename().
                                lastIndexOf('.'));
        String fullPath=logoPath+File.separator+name;

        FileOutputStream fileOutputStream=new FileOutputStream(fullPath);
        fileOutputStream.write(fileByte);
        fileOutputStream.flush();
        fileOutputStream.close();
        return name;
    }

    private boolean deleteLogo(String name){
        File file=new File(logoPath+ File.separator+name);
        return file.delete();
    }

}
