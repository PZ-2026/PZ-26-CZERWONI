package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.Size;

public record StudentNotesRequest(
        @Size(max = 500, message = "Notatka może mieć maksymalnie 500 znaków")
                String studentNotes) {}
