package pl.edu.ur.teachly.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Żądanie logowania użytkownika.
 *
 * @param email adres e-mail użytkownika
 * @param password hasło użytkownika
 */
public record LoginRequest(
        @NotBlank(message = "Email nie może być pusty")
                @Email(message = "Nieprawidłowy format email")
                String email,
        @NotBlank(message = "Hasło nie może być puste") String password) {}
