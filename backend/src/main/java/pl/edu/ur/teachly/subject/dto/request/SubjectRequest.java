package pl.edu.ur.teachly.subject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank(message = "Nazwa przedmiotu nie może być pusta")
                @Size(max = 100, message = "Nazwa przedmiotu nie może przekraczać 100 znaków")
                String subjectName,
        @NotNull(message = "Kategoria jest wymagana") Integer categoryId) {}
