package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record LessonRequest(
        @NotNull(message = "Tutor jest wymagany")
        Integer tutorId,

        @NotNull(message = "Przedmiot jest wymagany")
        Integer subjectId,

        @NotNull(message = "Data lekcji jest wymagana")
        LocalDate lessonDate,

        @NotNull(message = "Godzina rozpoczęcia jest wymagana")
        LocalTime timeFrom,

        @NotNull(message = "Godzina zakończenia jest wymagana")
        LocalTime timeTo,

        @NotNull(message = "Format lekcji jest wymagany")
        LessonFormat format,

        @NotNull(message = "Status lekcji jest wymagany")
        LessonStatus lessonStatus,

        String studentNotes,

        @NotNull(message = "Kwota jest wymagana")
        @DecimalMin(value = "0.0", inclusive = false, message = "Kwota musi być większa niż 0")
        BigDecimal amount

) {
    @AssertTrue(message = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) return true;
        else return timeTo.isAfter(timeFrom);
    }
}