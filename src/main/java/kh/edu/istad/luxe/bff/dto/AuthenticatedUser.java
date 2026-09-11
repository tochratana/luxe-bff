package kh.edu.istad.luxe.bff.dto;

public record AuthenticatedUser(
        String username,
        Boolean isAuthenticated
) {
}
