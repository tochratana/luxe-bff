package kh.edu.istad.luxe.bff.controller;

import kh.edu.istad.luxe.bff.dto.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private final AuthController authController = new AuthController();

    @Test
    void anonymousRequestIsNotAuthenticated() {
        AuthenticatedUser response = authController.getAuthenticatedUser(null);

        assertEquals("anonymous", response.username());
        assertEquals(false, response.isAuthenticated());
    }

    @Test
    void googleIdentityWithoutPreferredUsernameFallsBackToEmail() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getName()).thenReturn("keycloak-subject");
        when(principal.getAttributes()).thenReturn(Map.of("email", "customer@example.com"));

        AuthenticatedUser response = authController.getAuthenticatedUser(principal);

        assertEquals("customer@example.com", response.username());
        assertTrue(response.isAuthenticated());
    }

    @Test
    void identityWithoutUsernameOrEmailFallsBackToPrincipalName() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getName()).thenReturn("keycloak-subject");
        when(principal.getAttributes()).thenReturn(Map.of());

        AuthenticatedUser response = authController.getAuthenticatedUser(principal);

        assertEquals("keycloak-subject", response.username());
        assertTrue(response.isAuthenticated());
    }
}
