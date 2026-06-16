package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.edu.ur.teachly.common.enums.LessonStatus;

/** Żądanie zmiany statusu lekcji z opcjonalnymi notatkami korepetytora. */
public record LessonStatusRequest(
        @NotNull(message = "Status lekcji jest wymagany") LessonStatus lessonStatus,
        @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków") String tutorNotes) {}
