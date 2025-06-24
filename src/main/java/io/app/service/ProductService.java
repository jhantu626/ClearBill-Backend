package io.app.service;

import io.app.dto.ApiResponse;
import io.app.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    public ApiResponse createProduct(String token, ProductDto productDto, MultipartFile image) throws IOException;
    public List<ProductDto> getProducts(String token);
}
