package pl.edu.ur.teachly.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Żądanie dodania lub aktualizacji opinii o korepetytorze.
 *
 * <p>Ocena musi mieścić się w przedziale 1.0–5.0.
 */
public record ReviewRequest(
        @NotNull(message = "Tutor jest wymagany") Integer tutorId,
        @NotNull(message = "Ocena jest wymagana")
                @DecimalMin(value = "1.0", message = "Ocena minimalna to 1.0")
                @DecimalMax(value = "5.0", message = "Ocena maksymalna to 5.0")
                BigDecimal rating,
        @Size(max = 500, message = "Komentarz może mieć maksymalnie 500 znaków") String comment) {}
