package pl.edu.ur.teachly.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Imię nie może być puste") @Size(max = 50) String firstName,
        @NotBlank(message = "Nazwisko nie może być puste") @Size(max = 50) String lastName,
        @NotBlank(message = "Email nie może być pusty")
                @Email(message = "Niepoprawny format adresu email")
                String email,
        String phoneNumber,
        String password,
        String avatarUrl) {}
