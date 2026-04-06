package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TutorRequest(
        String bio,

        @NotNull(message = "Stawka godzinowa jest wymagana")
        @DecimalMin(value = "0.0", inclusive = false, message = "Stawka musi być większa niż 0")
        @DecimalMax(value = "10000.0", inclusive = true, message = "Stawka nie może być większa niż 10000")
        BigDecimal hourlyRate,

        @NotNull
        Boolean offersOnline,

        @NotNull
        Boolean offersInPerson
) {
}
