package pl.edu.ur.teachly.subject.dto.response;

public record SubjectResponse(
        Integer id, String subjectName, Integer categoryId, String categoryName) {}
