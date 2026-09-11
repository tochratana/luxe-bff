package kh.edu.istad.luxe.bff.controller;

import kh.edu.istad.luxe.bff.dto.AuthenticatedUser;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/is-authenticated")
    public AuthenticatedUser getAuthenticatedUser(
            @AuthenticationPrincipal OAuth2User principal
            ) {

        if (principal == null) {
            return new AuthenticatedUser("anonymous", false);
        }

        return new AuthenticatedUser(resolveUsername(principal), true);
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
