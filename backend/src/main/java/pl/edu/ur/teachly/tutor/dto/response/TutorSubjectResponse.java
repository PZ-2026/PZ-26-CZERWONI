package pl.edu.ur.teachly.tutor.dto.response;

/** Odpowiedź z danymi przedmiotu korepetytora wraz z obsługiwanymi poziomami nauczania. */
public record TutorSubjectResponse(
        Integer id,
        Integer subjectId,
        String subjectName,
        String categoryName,
        Boolean levelPrimary,
        Boolean levelHighSchool,
        Boolean levelUniversity,
        Boolean levelExamPrep,
        Boolean levelProfessional) {}
