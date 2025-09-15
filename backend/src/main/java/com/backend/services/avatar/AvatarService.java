package com.backend.services.avatar;

import com.backend.models.user.User;
import com.backend.services.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final FileStorageService fileStorageService;

    @Value("${app.upload.max-file-size}")
    private long maxFileSizeBytes;

    public String saveAvatar(User user, MultipartFile avatar) {
        validateFileSize(avatar);

        String extension = getFileExtension(avatar.getOriginalFilename());
        String filename = user.getId() + "_" + System.currentTimeMillis() + extension;

        return fileStorageService.save(avatar, "avatars", filename);
    }

    public void deleteAvatar(String avatarPath) {
        if (avatarPath != null) {
            fileStorageService.delete(avatarPath);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSizeBytes)
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
