package pl.edu.ur.teachly.tutor.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

/** Odpowiedź z danymi cyklicznego slotu dostępności korepetytora. */
public record TutorAvailabilityRecurringResponse(
        Integer id,
        Integer tutorId,
        int dayOfWeek,
        LocalTime timeFrom,
        LocalTime timeTo,
        LocalDate dateTo) {}
