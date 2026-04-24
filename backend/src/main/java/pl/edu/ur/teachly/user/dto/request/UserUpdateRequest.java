package pl.edu.ur.teachly.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Imię nie może być puste")
        @Size(max = 50)
        String firstName,
        
        @NotBlank(message = "Nazwisko nie może być puste")
        @Size(max = 50)
        String lastName,

        String avatarUrl) {
}
