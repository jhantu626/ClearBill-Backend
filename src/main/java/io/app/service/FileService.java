package io.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileService {
    public InputStream getBusinessImage(String fileName) throws FileNotFoundException;
}
