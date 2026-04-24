package pl.edu.ur.teachly.tutor.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record TutorSubjectRequest(
        @NotNull(message = "Przedmiot jest wymagany") Integer subjectId,
        @NotNull(message = "Poziom podstawowy musi być określony") Boolean levelPrimary,
        @NotNull(message = "Poziom licealny musi być określony") Boolean levelHighSchool,
        @NotNull(message = "Poziom akademicki musi być określony") Boolean levelUniversity,
        @NotNull(message = "Poziom przygotowania do egzaminów musi być określony")
        Boolean levelExamPrep,
        @NotNull(message = "Poziom zawodowy musi być określony") Boolean levelProfessional) {

    @AssertTrue(message = "Należy wybrać co najmniej jeden poziom nauczania")
    public boolean isAtLeastOneLevelSelected() {
        return Boolean.TRUE.equals(levelPrimary)
                || Boolean.TRUE.equals(levelHighSchool)
                || Boolean.TRUE.equals(levelUniversity)
                || Boolean.TRUE.equals(levelExamPrep)
                || Boolean.TRUE.equals(levelProfessional);
    }
}