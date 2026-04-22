package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.Size;

public record TutorNotesRequest(
        @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków") String tutorNotes) {}
