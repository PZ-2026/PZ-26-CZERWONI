package pl.edu.ur.teachly.auth.dto.response;

import pl.edu.ur.teachly.common.enums.UserRole;

public record AuthResponse(
        String token,
        String tokenType,
        UserRole role,
        Integer userId
) {
    public AuthResponse(String token, UserRole role, Integer userId) {
        this(token, "Bearer", role, userId);
    }
}
