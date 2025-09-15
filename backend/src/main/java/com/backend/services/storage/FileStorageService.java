package com.backend.services.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    String save(MultipartFile file, String subDir, String fileName);

    void delete(String relativePath);

    Path resolvePath(String relativePath);
}
