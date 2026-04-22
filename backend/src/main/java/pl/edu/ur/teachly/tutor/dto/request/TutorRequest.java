package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record TutorRequest(
        @Size(max = 2000, message = "Opis nie może przekraczać 2000 znaków") String bio,
        @NotNull(message = "Stawka godzinowa jest wymagana")
                @DecimalMin(
                        value = "0.0",
                        inclusive = false,
                        message = "Stawka musi być większa niż 0zł")
                @DecimalMax(value = "1000.0", message = "Stawka nie może być większa niż 1 000zł")
                BigDecimal hourlyRate,
        @NotNull Boolean offersOnline,
        @NotNull Boolean offersInPerson) {}
