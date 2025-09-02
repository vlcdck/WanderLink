package com.backend.mappers;

import com.backend.dto.user.UserDTO;
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
}
