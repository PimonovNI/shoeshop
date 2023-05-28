package com.example.shoeshop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
    public static void save(String imgPath, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(imgPath);

        if (!Files.exists(uploadPath))
            Files.createDirectory(uploadPath);

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e){
            throw new IOException("Can not saveShoes this image: " + e.getMessage());
        }
    }

    public static void delete(String imgPath, String fileName) throws IOException {
        Files.delete(Paths.get(imgPath).resolve(fileName));
    }
}
