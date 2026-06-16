package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Żądanie administracyjnej aktualizacji profilu korepetytora. */
public record TutorRequest(
        @Size(max = 2000, message = "Opis nie może przekraczać 2000 znaków") String bio,
        @NotNull(message = "Stawka godzinowa jest wymagana")
                @DecimalMin(
                        value = "1.0",
                        inclusive = true,
                        message = "Stawka musi wynosić co najmniej 1 zł")
                @DecimalMax(value = "1000.0", message = "Stawka nie może być większa niż 1 000zł")
                BigDecimal hourlyRate,
        @NotNull(message = "Informacja o lekcjach online jest wymagana") Boolean offersOnline,
        @NotNull(message = "Informacja o lekcjach stacjonarnych jest wymagana")
                Boolean offersInPerson,
        @Size(min = 2, max = 50, message = "Nazwa miasta musi mieć od 2 do 50 znaków")
                String city) {}
