package pl.edu.ur.teachly.holiday.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.entity.Holiday;

@Mapper(componentModel = "spring")
public interface HolidayMapper {
    HolidayResponse toResponse(Holiday holiday);

    @Mapping(target = "id", ignore = true)
    Holiday toEntity(HolidayRequest request);

    @Mapping(target = "id", ignore = true)
    void updateFromRequest(HolidayRequest request, @MappingTarget Holiday holiday);
}
