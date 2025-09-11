package com.backend.dto.user;

import com.backend.models.user.ExperienceLevel;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
    private ExperienceLevel experienceLevel;
    private Double weight;
    private Double height;
    private Boolean hasMedicalConditions;
    private String medicalNotes;
    private String phoneNumber;
}
