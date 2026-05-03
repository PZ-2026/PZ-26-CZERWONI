package pl.edu.ur.teachly.auth.dto.request;

import jakarta.validation.constraints.*;
import pl.edu.ur.teachly.common.enums.UserRole;

public record RegisterRequest(
        @NotNull(message = "Rola nie może być pusta") UserRole userRole,
        @NotBlank(message = "Imię nie może być puste") @Size(max = 50) String firstName,
        @NotBlank(message = "Nazwisko nie może być puste") @Size(max = 50) String lastName,
        @NotBlank(message = "Email nie może być pusty")
                @Email(message = "Nieprawidłowy format email")
                @Size(max = 100)
                String email,
        @NotNull(message = "Numer telefonu jest wymagany")
                @Pattern(regexp = "\\d{9}", message = "Numer telefonu musi składać się z 9 cyfr")
                String phoneNumber,
        @NotBlank(message = "Hasło nie może być puste")
                @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
                String password) {}
