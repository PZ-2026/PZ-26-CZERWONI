package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Żądanie dodania cyklicznego slotu dostępności korepetytora.
 *
 * <p>Dzień tygodnia zgodnie z ISO: 1 = poniedziałek, 7 = niedziela. Pole {@code dateTo} jest
 * opcjonalne — brak wartości oznacza dostępność bezterminową.
 */
public record TutorAvailabilityRecurringRequest(
        @NotNull(message = "Dzień tygodnia jest wymagany")
                @Min(
                        value = 1,
                        message = "Dzień tygodnia musi być od 1 (poniedziałek) do 7 (niedziela)")
                @Max(
                        value = 7,
                        message = "Dzień tygodnia musi być od 1 (poniedziałek) do 7 (niedziela)")
                Integer dayOfWeek,
        @NotNull(message = "Godzina rozpoczęcia jest wymagana") LocalTime timeFrom,
        @NotNull(message = "Godzina zakończenia jest wymagana") LocalTime timeTo,
        LocalDate dateTo) {

    @AssertTrue(message = "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) {
            return true;
        }
        return timeTo.isAfter(timeFrom);
    }
}
