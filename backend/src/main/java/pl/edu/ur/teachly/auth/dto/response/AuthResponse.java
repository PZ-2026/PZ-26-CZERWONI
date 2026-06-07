package pl.edu.ur.teachly.auth.dto.response;

import pl.edu.ur.teachly.common.enums.UserRole;

/**
 * Odpowiedź zwracana po pomyślnym uwierzytelnieniu lub rejestracji.
 *
 * @param token podpisany token JWT
 * @param tokenType typ tokenu (zawsze {@code "Bearer"})
 * @param role rola uwierzytelnionego użytkownika
 * @param userId identyfikator uwierzytelnionego użytkownika
 */
public record AuthResponse(String token, String tokenType, UserRole role, Integer userId) {
    public AuthResponse(String token, UserRole role, Integer userId) {
        this(token, "Bearer", role, userId);
    }
}
