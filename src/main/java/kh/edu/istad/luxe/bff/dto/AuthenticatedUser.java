package kh.edu.istad.luxe.bff.dto;

import lombok.Builder;

@Builder
public record AuthenticatedUser(
        String username,
        Boolean isAuthenticated
) {
}
