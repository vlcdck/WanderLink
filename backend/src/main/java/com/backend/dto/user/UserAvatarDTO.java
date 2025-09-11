package com.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserAvatarDTO {

    @NotNull(message = "Avatar file is required")
    private MultipartFile avatar;
}
