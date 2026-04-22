package pl.edu.ur.teachly.holiday.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record HolidayRequest(
        @NotNull(message = "Data święta jest wymagana") LocalDate holidayDate,
        @Size(max = 100, message = "Opis nie może przekraczać 100 znaków") String description) {}
