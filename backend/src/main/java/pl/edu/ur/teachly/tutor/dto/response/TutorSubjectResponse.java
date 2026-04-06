package pl.edu.ur.teachly.tutor.dto.response;

public record TutorSubjectResponse(
        Integer id,
        Integer subjectId,
        String subjectName,
        String categoryName,
        Boolean levelPrimary,
        Boolean levelHighSchool,
        Boolean levelUniversity,
        Boolean levelExamPrep,
        Boolean levelProfessional
) {
}
