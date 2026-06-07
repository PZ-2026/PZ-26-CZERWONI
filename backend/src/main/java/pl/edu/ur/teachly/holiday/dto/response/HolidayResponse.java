package pl.edu.ur.teachly.holiday.dto.response;

import java.time.LocalDate;

/** Odpowiedź z danymi dnia wolnego. */
public record HolidayResponse(Integer id, LocalDate holidayDate, String description) {}
