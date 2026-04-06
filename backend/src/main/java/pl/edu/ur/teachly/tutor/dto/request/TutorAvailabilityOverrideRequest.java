package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorAvailabilityOverrideRequest(
        @NotNull(message = "Data jest wymagana")
        LocalDate overrideDate,

        LocalTime timeFrom,

        LocalTime timeTo
) {
}
