package pl.edu.ur.teachly.tutor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityOverrideRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityRecurringRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityOverrideResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityRecurringResponse;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;

@Mapper(componentModel = "spring")
public interface TutorAvailabilityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutor", ignore = true)
    TutorAvailabilityOverride toEntity(TutorAvailabilityOverrideRequest request);

    @Mapping(source = "tutor.userId", target = "tutorId")
    TutorAvailabilityOverrideResponse toResponse(TutorAvailabilityOverride entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutor", ignore = true)
    TutorAvailabilityRecurring toEntity(TutorAvailabilityRecurringRequest request);

    @Mapping(source = "tutor.userId", target = "tutorId")
    TutorAvailabilityRecurringResponse toResponse(TutorAvailabilityRecurring entity);
}
