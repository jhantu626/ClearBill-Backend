package io.app.service.impl;

import io.app.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.logo}")
    private String businessDirectoryPath;

    @Value("${file.product.image}")
    private String productImagePath;

    @Override
    public InputStream getBusinessImage(String fileName) throws FileNotFoundException {
        FileInputStream fileInputStream=new FileInputStream(businessDirectoryPath+ File.separator+fileName);
        return fileInputStream;
    }

    @Override
    public String uploadProductImage(MultipartFile file) throws IOException {
        File folder=new File(productImagePath);
        if (!folder.exists()){
            folder.mkdirs();
        }

        String name= UUID.randomUUID().toString()+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));

        byte[] fileBinary=file.getBytes();
        FileOutputStream fileOutputStream=new FileOutputStream(folder+File.separator+name);
        fileOutputStream.write(fileBinary);
        fileOutputStream.flush();
        fileOutputStream.close();
        return name;
    }


}
