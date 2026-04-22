package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;

public record LessonRequest(
        @NotNull(message = "Tutor jest wymagany") Integer tutorId,
        @NotNull(message = "Przedmiot jest wymagany") Integer subjectId,
        @NotNull(message = "Data lekcji jest wymagana") LocalDate lessonDate,
        @NotNull(message = "Godzina rozpoczęcia jest wymagana") LocalTime timeFrom,
        @NotNull(message = "Godzina zakończenia jest wymagana") LocalTime timeTo,
        @NotNull(message = "Format lekcji jest wymagany") LessonFormat format,
        @NotNull(message = "Status lekcji jest wymagany") LessonStatus lessonStatus,
        @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków") String studentNotes,
        @NotNull(message = "Kwota jest wymagana")
                @DecimalMin(
                        value = "0.0",
                        inclusive = false,
                        message = "Kwota musi być większa niż 0")
                @DecimalMax(value = "1000.0", message = "Kwota nie może być większa niż 1 000zł")
                BigDecimal amount) {

    @AssertTrue(message = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) {
            return true;
        } else {
            return timeTo.isAfter(timeFrom);
        }
    }
}
