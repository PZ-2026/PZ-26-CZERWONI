package pl.edu.ur.teachly.tutor.dto.response;

import java.util.List;

/** Wynik wyszukiwania korepetytora wzbogacony o listę przedmiotów i statystyki ocen. */
public record TutorSearchResultResponse(
        TutorResponse tutor,
        List<TutorSubjectResponse> subjects,
        Double averageRating,
        Integer reviewCount) {}
