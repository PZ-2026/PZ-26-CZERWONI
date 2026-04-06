package pl.edu.ur.teachly.tutor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;

@Mapper(componentModel = "spring")
public interface TutorMapper {
    TutorResponse toResponse(Tutor tutor);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Tutor toEntity(TutorRequest request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateFromRequest(TutorRequest request, @MappingTarget Tutor tutor);
}
