package com.backend.mappers;

import com.backend.dto.user.UserDTO;
import com.backend.dto.user.UserProfileDTO;
import com.backend.models.user.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    public static UserProfileDTO toProfileDTO(User user) {
        if (user == null) return null;

        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setExperienceLevel(user.getExperienceLevel());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setHasMedicalConditions(user.getHasMedicalConditions());
        dto.setMedicalNotes(user.getMedicalNotes());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }
}
