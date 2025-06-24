package io.app.service;

import io.app.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {
    public InputStream getBusinessImage(String fileName) throws FileNotFoundException;
    public String uploadProductImage(MultipartFile file) throws IOException;
    public InputStream serveProductImage(String fileName) throws FileNotFoundException;
}
