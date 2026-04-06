package pl.edu.ur.teachly.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank(message = "Aktualne hasło jest wymagane")
        String currentPassword,

        @NotBlank(message = "Nowe hasło nie może być puste")
        @Size(min = 8, message = "Nowe hasło musi mieć co najmniej 8 znaków")
        String newPassword
) {
}
