package com.backend.services.user;

import com.backend.dto.hike.HikeDTO;
import com.backend.dto.user.ChangePasswordDTO;
import com.backend.dto.user.UserProfileDTO;
import com.backend.dto.user.UserProfileUpdateDTO;
import com.backend.mappers.HikeMapper;
import com.backend.mappers.UserMapper;
import com.backend.models.user.User;
import com.backend.repository.UserRepository;
import com.backend.services.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;

    public UserProfileDTO getProfile(User user) {
        return UserMapper.toProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateProfile(User user, UserProfileUpdateDTO dto) {
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
        return UserMapper.toProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateAvatar(User user, MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) return UserMapper.toProfileDTO(user);

        avatarService.deleteAvatar(user.getAvatarUrl());
        String relativePath = avatarService.saveAvatar(user, avatar);
        user.setAvatarUrl(relativePath);

        userRepository.save(user);
        return UserMapper.toProfileDTO(user);
    }

    @Transactional
    public void deleteAvatar(User user) {
        avatarService.deleteAvatar(user.getAvatarUrl());
        user.setAvatarUrl(null);
        userRepository.save(user);
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