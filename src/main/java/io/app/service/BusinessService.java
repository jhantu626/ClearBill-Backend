package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.BusinessDto;
import io.app.model.Business;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BusinessService {
    public ApiResponse createBusiness(BusinessDto business, MultipartFile logo,String token) throws IOException;
    public BusinessDto getBusiness(String token);
    public ApiResponse updateBusiness(BusinessDto business,
                                      MultipartFile image) throws IOException;
}
