package pl.edu.ur.teachly.subject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectCategoryRequest(
        @NotBlank(message = "Nazwa kategorii nie może być pusta")
                @Size(max = 100, message = "Nazwa kategorii nie może przekraczać 100 znaków")
                String categoryName) {}
