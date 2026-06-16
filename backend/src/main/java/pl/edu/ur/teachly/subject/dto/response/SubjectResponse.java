package pl.edu.ur.teachly.subject.dto.response;

/** Odpowiedź z danymi przedmiotu, zawierająca nazwę i dane kategorii nadrzędnej. */
public record SubjectResponse(
        Integer id, String subjectName, Integer categoryId, String categoryName) {}
