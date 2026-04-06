package pl.edu.ur.teachly.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Imię nie może być puste")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Nazwisko nie może być puste")
        @Size(max = 50)
        String lastName,

        @NotBlank(message = "Email nie może być pusty")
        @Email(message = "Nieprawidłowy format email")
        @Size(max = 100)
        String email,

        @NotBlank(message = "Numer telefonu nie może być pusty")
        @Size(min = 10, max = 10, message = "Numer telefonu musi mieć 10 cyfr")
        String phoneNumber,

        @NotBlank(message = "Hasło nie może być puste")
        @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
        String password
) {
}
