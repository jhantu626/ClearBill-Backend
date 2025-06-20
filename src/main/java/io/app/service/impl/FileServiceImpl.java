package io.app.service.impl;

import io.app.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.logo}")
    String businessDirectoryPath;

    @Override
    public InputStream getBusinessImage(String fileName) throws FileNotFoundException {
        FileInputStream fileInputStream=new FileInputStream(businessDirectoryPath+ File.separator+fileName);
        return fileInputStream;
    }
}
