package pl.edu.ur.teachly.subject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.entity.Subject;

/**
 * Mapper MapStruct konwertujący między encją {@link Subject} a jej DTO.
 *
 * <p>Mapuje zagnieżdżone pola kategorii ({@code category.id}, {@code category.categoryName}) na
 * płaskie pola odpowiedzi {@link SubjectResponse}.
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    SubjectResponse toResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Subject toEntity(SubjectRequest request);
}
