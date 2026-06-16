package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Żądanie samodzielnej aktualizacji profilu przez zalogowanego korepetytora.
 *
 * <p>Wymaga co najmniej jednej formy zajęć i przynajmniej jednego przypisanego przedmiotu
 * (walidacja po stronie serwisu).
 */
public record TutorSelfProfileRequest(
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
        @Size(max = 100, message = "Nazwa miasta nie może przekraczać 100 znaków") String city) {}
