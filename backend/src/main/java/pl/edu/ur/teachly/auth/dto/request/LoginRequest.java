package pl.edu.ur.teachly.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email nie może być pusty")
        @Email(message = "Nieprawidłowy format email")
        String email,

        @NotBlank(message = "Hasło nie może być puste")
        String password
) {
}
