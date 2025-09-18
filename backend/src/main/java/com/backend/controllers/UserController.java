package com.backend.controllers;

import com.backend.dto.hike.HikeDTO;
import com.backend.dto.user.ChangePasswordDTO;
import com.backend.dto.user.UserAvatarDTO;
import com.backend.dto.user.UserProfileDTO;
import com.backend.dto.user.UserProfileUpdateDTO;
import com.backend.security.UserPrincipal;
import com.backend.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getUser()));
    }

    // Часткове оновлення текстових даних
    @PatchMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserProfileUpdateDTO dto
    ) {
        UserProfileDTO updatedProfile = userService.updateProfile(principal.getUser(), dto);
        return ResponseEntity.ok(updatedProfile);
    }


    // Окремо зміна пароля
    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordDTO dto
    ) {
        try {
            userService.changePassword(principal.getUser(), dto);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Окремо аватарка
    @PatchMapping("/me/avatar")
    public ResponseEntity<UserProfileDTO> updateAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("avatar") MultipartFile avatar
    ) {
        UserProfileDTO updatedProfile = userService.updateAvatar(principal.getUser(), avatar);
        return ResponseEntity.ok(updatedProfile); // 🔥 повертаємо профіль
    }



    @DeleteMapping("me/avatar")
    public ResponseEntity<String> deleteAvatar(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteAvatar(principal.getUser());
        return ResponseEntity.ok("Avatar deleted successfully");
    }

    @GetMapping("/me/hikes/participated")
    public ResponseEntity<List<HikeDTO>> getParticipatedHikes(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getParticipatedHikes(principal.getUser().getId()));
    }

    @GetMapping("/me/hikes/organized")
    public ResponseEntity<List<HikeDTO>> getOrganizedHikes(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getOrganizedHikes(principal.getUser().getId()));
    }
}
