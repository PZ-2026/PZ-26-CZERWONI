package pl.edu.ur.teachly.subject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.subject.dto.request.SubjectCategoryRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectCategoryResponse;
import pl.edu.ur.teachly.subject.entity.SubjectCategory;

@Mapper(componentModel = "spring")
public interface SubjectCategoryMapper {
    SubjectCategoryResponse toResponse(SubjectCategory category);

    @Mapping(target = "id", ignore = true)
    SubjectCategory toEntity(SubjectCategoryRequest request);
}
