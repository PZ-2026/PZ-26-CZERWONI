package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorAvailabilityRecurringRequest(
        @NotNull(message = "Dzień tygodnia jest wymagany")
        @Min(
                value = 0,
                message = "Dzień tygodnia musi być od 0 (poniedziałek) do 6 (niedziela)")
        @Max(
                value = 6,
                message = "Dzień tygodnia musi być od 0 (poniedziałek) do 6 (niedziela)")
        Integer dayOfWeek,
        @NotNull(message = "Godzina rozpoczęcia jest wymagana") LocalTime timeFrom,
        @NotNull(message = "Godzina zakończenia jest wymagana") LocalTime timeTo,
        LocalDate dateTo) {
    
    @AssertTrue(message = "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) return true;
        return timeTo.isAfter(timeFrom);
    }
}
