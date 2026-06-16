package pl.edu.ur.teachly.tutor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;

/**
 * Mapper MapStruct konwertujący encję {@link TutorSubject} na DTO odpowiedzi.
 *
 * <p>Mapuje zagnieżdżone pola przedmiotu i jego kategorii na płaskie pola odpowiedzi {@link
 * TutorSubjectResponse}.
 */
@Mapper(componentModel = "spring")
public interface TutorSubjectMapper {

    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    @Mapping(source = "subject.category.categoryName", target = "categoryName")
    TutorSubjectResponse toResponse(TutorSubject tutorSubject);
}
