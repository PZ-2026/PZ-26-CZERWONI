package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorAvailabilityOverrideRequest(
        @NotNull(message = "Data jest wymagana") LocalDate overrideDate,
        LocalTime timeFrom,
        LocalTime timeTo) {
    @AssertTrue(message = "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) return true;
        return timeTo.isAfter(timeFrom);
    }
}
