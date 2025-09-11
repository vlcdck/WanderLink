package com.backend.services.user;

import com.backend.dto.hike.HikeDTO;
import com.backend.dto.user.ChangePasswordDTO;
import com.backend.dto.user.UserProfileDTO;
import com.backend.dto.user.UserProfileUpdateDTO;
import com.backend.mappers.HikeMapper;
import com.backend.mappers.UserMapper;
import com.backend.models.user.User;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.avatar-dir}")
    private String avatarUploadDir;

    @Value("${app.upload.max-file-size}")
    private long maxFileSizeBytes;

    public UserProfileDTO getProfile(User user) {
        return UserMapper.toProfileDTO(user);
    }

    @Transactional
    public void updateProfile(User user, UserProfileUpdateDTO dto) {
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getExperienceLevel() != null) user.setExperienceLevel(dto.getExperienceLevel());
        if (dto.getWeight() != null) user.setWeight(dto.getWeight());
        if (dto.getHeight() != null) user.setHeight(dto.getHeight());
        if (dto.getHasMedicalConditions() != null) user.setHasMedicalConditions(dto.getHasMedicalConditions());
        if (dto.getMedicalNotes() != null) user.setMedicalNotes(dto.getMedicalNotes());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(user);
    }

    @Transactional
    public void updateAvatar(User user, MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) return;

        validateFileSize(avatar);
        try {
            String filename = storeAvatar(avatar, user.getId());
            user.setAvatarUrl("/" + avatarUploadDir + filename);
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Failed to store avatar for user {}", user.getId(), e);
            throw new RuntimeException("Failed to store avatar file", e);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSizeBytes)
            );
        }
    }

    private String storeAvatar(MultipartFile avatar, Long userId) throws IOException {
        Path uploadPath = Paths.get(avatarUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Генеруємо унікальне ім'я файлу
        String originalFilename = avatar.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = userId + "_" + System.currentTimeMillis() + fileExtension;

        Path filePath = uploadPath.resolve(filename);
        avatar.transferTo(filePath.toFile());

        return filename;
    }

    @Transactional
    public void changePassword(User user, ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<HikeDTO> getParticipatedHikes(Long userId) {
        User user = userRepository.findWithParticipatedHikesById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getParticipatedHikes().stream()
                .map(HikeMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<HikeDTO> getOrganizedHikes(Long userId) {
        User user = userRepository.findWithOrganizedHikesById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getOrganizedHikes().stream()
                .map(HikeMapper::toDTO)
                .toList();
    }
}
