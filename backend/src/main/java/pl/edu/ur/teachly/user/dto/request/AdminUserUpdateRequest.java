package pl.edu.ur.teachly.user.dto.request;

import jakarta.validation.constraints.*;
import pl.edu.ur.teachly.common.enums.UserRole;

public record AdminUserUpdateRequest(
        @NotBlank(message = "Imię nie może być puste")
                @Size(max = 50, message = "Imię nie może przekraczać 50 znaków")
                String firstName,
        @NotBlank(message = "Nazwisko nie może być puste")
                @Size(max = 50, message = "Nazwisko nie może przekraczać 50 znaków")
                String lastName,
        @NotBlank(message = "Email nie może być pusty")
                @Email(message = "Nieprawidłowy format email")
                @Size(max = 100, message = "Email nie może przekraczać 100 znaków")
                String email,
        @NotBlank(message = "Numer telefonu nie może być pusty")
                @Pattern(regexp = "\\d{9}", message = "Numer telefonu musi składać się z 9 cyfr")
                String phoneNumber,
        @NotNull(message = "Rola użytkownika jest wymagana") UserRole userRole,
        String avatarUrl) {}
