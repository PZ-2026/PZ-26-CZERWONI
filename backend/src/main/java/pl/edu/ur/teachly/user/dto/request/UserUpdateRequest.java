package pl.edu.ur.teachly.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Żądanie aktualizacji profilu użytkownika przez samego użytkownika.
 *
 * <p>Pole {@code password} jest opcjonalne — pominięcie lub pusta wartość oznacza brak zmiany
 * hasła.
 */
public record UserUpdateRequest(
        @NotBlank(message = "Imię nie może być puste")
                @Size(max = 50)
                @Pattern(
                        regexp = "[\\p{L} '\\-]+",
                        message = "Imię może zawierać tylko litery, spacje i myślniki")
                String firstName,
        @NotBlank(message = "Nazwisko nie może być puste")
                @Size(max = 50)
                @Pattern(
                        regexp = "[\\p{L} '\\-]+",
                        message = "Nazwisko może zawierać tylko litery, spacje i myślniki")
                String lastName,
        @NotBlank(message = "Email nie może być pusty")
                @Email(message = "Niepoprawny format adresu email")
                String email,
        @Pattern(regexp = "\\d{9}|", message = "Numer telefonu musi składać się z 9 cyfr")
                String phoneNumber,
        @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
                @Pattern(regexp = "\\S+|", message = "Hasło nie może zawierać spacji")
                String password,
        String avatarUrl) {}
