package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.edu.ur.teachly.common.enums.LessonStatus;

public record LessonStatusRequest(
        @NotNull(message = "Status lekcji jest wymagany")
        LessonStatus lessonStatus,

        String tutorNotes
) {
}
