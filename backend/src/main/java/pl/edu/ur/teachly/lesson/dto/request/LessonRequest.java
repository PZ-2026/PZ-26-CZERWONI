package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import pl.edu.ur.teachly.common.enums.LessonFormat;

/**
 * Żądanie utworzenia nowej lekcji przez ucznia.
 *
 * <p>Czas zakończenia musi być późniejszy niż czas rozpoczęcia (walidacja {@code @AssertTrue}).
 */
public record LessonRequest(
        @NotNull(message = "Tutor jest wymagany") Integer tutorId,
        @NotNull(message = "Przedmiot jest wymagany") Integer subjectId,
        @NotNull(message = "Data lekcji jest wymagana") LocalDate lessonDate,
        @NotNull(message = "Godzina rozpoczęcia jest wymagana") LocalTime timeFrom,
        @NotNull(message = "Godzina zakończenia jest wymagana") LocalTime timeTo,
        @NotNull(message = "Format lekcji jest wymagany") LessonFormat format,
        @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków")
                String studentNotes) {

    @AssertTrue(message = "Czas zakończenia musi być późniejszy niż czas rozpoczęcia")
    public boolean isTimeValid() {
        if (timeFrom == null || timeTo == null) {
            return true;
        } else {
            return timeTo.isAfter(timeFrom);
        }
    }
}
