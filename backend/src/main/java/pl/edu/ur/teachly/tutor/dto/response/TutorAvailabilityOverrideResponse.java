package pl.edu.ur.teachly.tutor.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorAvailabilityOverrideResponse(
        Integer id,
        Integer tutorId,
        LocalDate overrideDate,
        LocalTime timeFrom,
        LocalTime timeTo) {}
