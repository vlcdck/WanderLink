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

import java.util.List;

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
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserProfileUpdateDTO dto
    ) {
        userService.updateProfile(principal.getUser(), dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // Окремо зміна пароля
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(principal.getUser(), dto);
        return ResponseEntity.ok("Password changed successfully");
    }

    // Окремо аватарка
    @PatchMapping("/me/avatar")
    public ResponseEntity<String> updateAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @ModelAttribute UserAvatarDTO dto
    ) {
        userService.updateAvatar(principal.getUser(), dto.getAvatar());
        return ResponseEntity.ok("Avatar updated successfully");
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
