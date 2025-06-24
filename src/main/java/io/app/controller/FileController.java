package io.app.controller;

import io.app.service.impl.FileServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private final FileServiceImpl service;


    @GetMapping("/business/logo/{name}")
    public void getBusinessLogo(@PathVariable("name") String name,
                                HttpServletResponse response) throws IOException {
        InputStream image=service.getBusinessImage(name);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(image,response.getOutputStream());
    }

    @GetMapping("/product/{name}")
    public void serveProductImage(
            @PathVariable("name") String name,
            HttpServletResponse response) throws IOException {
        InputStream image=service.serveProductImage(name);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(image,response.getOutputStream());
    }
}
