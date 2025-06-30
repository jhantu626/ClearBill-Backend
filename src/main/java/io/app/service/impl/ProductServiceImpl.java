package io.app.service.impl;

import io.app.dto.ApiResponse;
import io.app.dto.ProductDto;
import io.app.exception.ResourceNotFoundException;
import io.app.model.Business;
import io.app.model.Product;
import io.app.repository.ProductRepository;
import io.app.repository.UserRepository;
import io.app.service.JwtService;
import io.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FileServiceImpl fileService;

    @Override
    public ApiResponse createProduct(String token, ProductDto product, MultipartFile image) throws IOException {
        Business business=userRepository.findBusinessByEmail(extractUsername(token))
                .orElseThrow(()->new ResourceNotFoundException("No Business Found!"));
        String imageName=fileService.uploadProductImage(image);
        Product newProduct=Product.builder()
                .name(product.getName())
                .description(product.getDescription().trim())
                .isTaxable(product.getIsTaxable()
                )
                .logo(imageName)
                .iGst(product.getIGst())
                .cGst(product.getCGst())
                .sGst(product.getSGst())
                .hsnCode(product.getHsnCode())
                .unitType(product.getUnitType())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .business(business)
                .build();
        repository.save(newProduct);
        return ApiResponse.builder()
                .message("Product Added Successfully")
                .status(true)
                .build();
    }

    @Override
    public List<ProductDto> getProducts(String token) {
        String userEmail=extractUsername(token);
        long businessId=userRepository.findBusinessIdByEmail(userEmail)
                .orElseThrow(()->new ResourceNotFoundException("Business Not Found"));
        Business business= Business.builder()
                .id(businessId)
                .build();
        List<Product> products=repository.findByBusiness(business);
        return products.stream().map((item)->{
            ProductDto productDto=ProductDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .name(item.getName())
                    .cGst(item.getCGst())
                    .sGst(item.getSGst())
                    .iGst(item.getIGst())
                    .isTaxable(item.isTaxable())
                    .price(item.getPrice())
                    .unitType(item.getUnitType())
                    .discount(item.getDiscount())
                    .hsnCode(item.getHsnCode())
                    .createdAt(item.getCreatedAt())
                    .updateAt(item.getUpdateAt())
                    .logo(item.getLogo())
                    .build();
            return productDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ApiResponse updateProduct(ProductDto productDto) {
        Product product=repository.findById(productDto.getId())
                .orElseThrow(()->new ResourceNotFoundException("Invalid Product"));
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription().trim());
        product.setTaxable(productDto.getIsTaxable());
        product.setHsnCode(productDto.getHsnCode());
        product.setSGst(productDto.getSGst());
        product.setCGst(productDto.getCGst());
        product.setIGst(productDto.getIGst());
        product.setDiscount(productDto.getDiscount());
        product.setPrice(productDto.getPrice());
        product.setUnitType(productDto.getUnitType());
        repository.save(product);
        return ApiResponse.builder()
                .status(true)
                .message("Updated Successfully")
                .build();
    }


    @Override
    public ApiResponse updateProduct(ProductDto productDto,MultipartFile image) throws IOException {
        Product product=repository.findById(productDto.getId())
                .orElseThrow(()->new ResourceNotFoundException("Invalid Product"));
        if (fileService.deleteProductImage(product.getLogo())){
            String logoName=fileService.uploadProductImage(image);
            product.setLogo(logoName);
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription().trim());
        product.setTaxable(productDto.getIsTaxable());
        product.setHsnCode(productDto.getHsnCode());
        product.setSGst(productDto.getSGst());
        product.setCGst(productDto.getCGst());
        product.setIGst(productDto.getIGst());
        product.setDiscount(productDto.getDiscount());
        product.setPrice(productDto.getPrice());
        product.setUnitType(productDto.getUnitType());
        repository.save(product);
        return ApiResponse.builder()
                .status(true)
                .message("Updated Successfully")
                .build();
    }


    private String extractUsername(String token){
        token=token.substring(7);
        return jwtService.extractUsername(token);
    }

}
