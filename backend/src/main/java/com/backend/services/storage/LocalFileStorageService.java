package com.backend.services.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.upload.base-dir}")
    private String baseDir;

    public String save(MultipartFile file, String subDir, String fileName) {
        try {
            Path uploadPath = Paths.get(baseDir, subDir);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath);

            return subDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }

    public void delete(String relativePath) {
        try {
            Path filePath = resolvePath(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося видалити файл: " + relativePath, e);
        }
    }

    public Path resolvePath(String relativePath) {
        return Paths.get(baseDir).resolve(relativePath);
    }
}
