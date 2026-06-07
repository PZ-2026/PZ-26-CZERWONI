package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Żądanie dodania jednorazowego nadpisania dostępności korepetytora.
 *
 * <p>Pola {@code timeFrom} i {@code timeTo} są opcjonalne — ich brak oznacza zablokowanie całego
 * dnia (korepetytor niedostępny).
 */
public record TutorAvailabilityOverrideRequest(
        @NotNull(message = "Data jest wymagana") LocalDate overrideDate,
        LocalTime timeFrom,
        LocalTime timeTo) {
    @AssertTrue(message = "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) {
            return true;
        }
        return timeTo.isAfter(timeFrom);
    }
}
