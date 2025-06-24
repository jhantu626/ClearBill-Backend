package io.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.app.dto.ApiResponse;
import io.app.dto.ProductDto;
import io.app.service.ProductService;
import io.app.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceImpl service;

    @PostMapping
    public ResponseEntity<ApiResponse> addProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("product") String product,
            @RequestParam("image") MultipartFile image
            ) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        ProductDto productDto=objectMapper.readValue(product,ProductDto.class);
        return new ResponseEntity<>(service.createProduct(token,productDto,image), HttpStatus.CREATED);
    }

    /*
    * Get ALL Products of business
    * EndPoint: /api/v1/product
    * Method: GET
    */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestHeader("Authorization") String authToken){
        return ResponseEntity.ok(service.getProducts(authToken));
    }

    /*
    * Update Product Without Image
    * Endpoint: /api/v1/product
    * Method: PUT
    */
    @PutMapping
    public ResponseEntity<ApiResponse> updateProductWithoutImage(@RequestBody ProductDto productDto){
        return ResponseEntity.ok(service.updateProduct(productDto));
    }

    /*
     * Update Product With Image
     * Endpoint: /api/v1/product/image
     * Method: PUT
     */
    @PutMapping("/image")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestParam("product") String product,
            @RequestParam("image") MultipartFile image) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        ProductDto productDto=objectMapper.readValue(product,ProductDto.class);
        return ResponseEntity.ok(service.updateProduct(productDto,image));
    }
}
