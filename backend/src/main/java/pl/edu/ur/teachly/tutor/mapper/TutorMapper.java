package pl.edu.ur.teachly.tutor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;

@Mapper(componentModel = "spring")
public interface TutorMapper {
    @Mapping(source = "userId", target = "id")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    TutorResponse toResponse(Tutor tutor);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Tutor toEntity(TutorRequest request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateFromRequest(TutorRequest request, @MappingTarget Tutor tutor);
}
