package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.NotNull;

public record TutorSubjectRequest(
        @NotNull(message = "Przedmiot jest wymagany") Integer subjectId,
        Boolean levelPrimary,
        Boolean levelHighSchool,
        Boolean levelUniversity,
        Boolean levelExamPrep,
        Boolean levelProfessional) {}
