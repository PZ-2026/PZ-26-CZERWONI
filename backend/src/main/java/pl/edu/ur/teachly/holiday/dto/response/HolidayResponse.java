package pl.edu.ur.teachly.holiday.dto.response;

import java.time.LocalDate;

public record HolidayResponse(
        Integer id,
        LocalDate holidayDate,
        String description
) {
}
