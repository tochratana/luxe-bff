package kh.edu.istad.luxe.bff.controller;

import kh.edu.istad.luxe.bff.dto.AuthenticatedUser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    @GetMapping("/is-authenticated")
    public AuthenticatedUser getAuthenticatedUser(
            @AuthenticationPrincipal OAuth2User principal
            ) {

        if (principal == null) { // 401
            return AuthenticatedUser.builder()
                    .username("anonymous")
                    .isAuthenticated(false)
                    .build();
        }

        log.info("getAuthenticatedUser: {}", principal);
        log.info("keycloak userId: {}", principal.getName());
        log.info("Username: {}", principal.getAttributes().get("preferred_username"));

        return AuthenticatedUser.builder()
                .username(resolveUsername(principal))
                .isAuthenticated(true)
                .build();
    }

    private String resolveUsername(OAuth2User principal) {
        Object preferredUsername = principal.getAttributes().get("preferred_username");
        if (preferredUsername instanceof String username && !username.isBlank()) {
            return username;
        }

        Object email = principal.getAttributes().get("email");
        if (email instanceof String emailAddress && !emailAddress.isBlank()) {
            return emailAddress;
        }

        return principal.getName();
    }
}
