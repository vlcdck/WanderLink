package com.backend.security;

import com.backend.dto.auth.AuthResponse;
import com.backend.services.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ApplicationContext context; // ✅ замість AuthService напряму

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        AuthService authService = context.getBean(AuthService.class);
        AuthResponse tokens = authService.loginOrRegisterGoogle(email, name);

        // 🔑 редіректимо на фронт з токенами
        String redirectUrl = "http://localhost:5173/oauth2/redirect" +
                "?accessToken=" + tokens.getAccessToken() +
                "&refreshToken=" + tokens.getRefreshToken();

        response.sendRedirect(redirectUrl);
    }
}
