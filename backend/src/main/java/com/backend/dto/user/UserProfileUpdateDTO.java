package com.backend.dto.user;

import com.backend.models.user.ExperienceLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileUpdateDTO {

    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @Size(max = 30, message = "Username must be at most 30 characters")
    private String username;

    @Size(max = 250, message = "Bio must be at most 250 characters")
    private String bio;

    private ExperienceLevel experienceLevel;

    @DecimalMin(value = "30.0", message = "Weight must be at least 30kg")
    @DecimalMax(value = "300.0", message = "Weight must be at most 300kg")
    private Double weight;

    @DecimalMin(value = "100.0", message = "Height must be at least 100cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250cm")
    private Double height;

    private Boolean hasMedicalConditions;

    @Size(max = 500, message = "Medical notes must be at most 500 characters")
    private String medicalNotes;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
    private String phoneNumber;
}
