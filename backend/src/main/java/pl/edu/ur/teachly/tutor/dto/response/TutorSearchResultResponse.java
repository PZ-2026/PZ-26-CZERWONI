package pl.edu.ur.teachly.tutor.dto.response;

import java.util.List;

public record TutorSearchResultResponse(
        TutorResponse tutor,
        List<TutorSubjectResponse> subjects,
        Double averageRating,
        Integer reviewCount) {}
