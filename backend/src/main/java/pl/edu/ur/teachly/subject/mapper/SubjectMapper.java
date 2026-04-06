package pl.edu.ur.teachly.subject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.entity.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    @Mapping(source = "category.id", target = "categoryId")
    SubjectResponse toResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Subject toEntity(SubjectRequest request);
}
