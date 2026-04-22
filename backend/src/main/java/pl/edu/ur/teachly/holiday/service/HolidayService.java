package pl.edu.ur.teachly.holiday.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.entity.Holiday;
import pl.edu.ur.teachly.holiday.mapper.HolidayMapper;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    @Transactional(readOnly = true)
    public List<HolidayResponse> getAllHolidays() {
        return holidayRepository.findAll().stream().map(holidayMapper::toResponse).toList();
    }

    @Transactional
    public HolidayResponse addHoliday(HolidayRequest request) {
        if (holidayRepository.existsByHolidayDate(request.holidayDate())) {
            throw new BusinessValidationException("Święto w tym dniu zostało już dodane");
        }
        Holiday holiday = holidayMapper.toEntity(request);
        return holidayMapper.toResponse(holidayRepository.save(holiday));
    }

    @Transactional
    public HolidayResponse updateHoliday(Integer holidayId, HolidayRequest request) {
        Holiday holiday =
                holidayRepository
                        .findById(holidayId)
                        .orElseThrow(
                                () ->
                                        new pl.edu.ur.teachly.common.exception
                                                .ResourceNotFoundException(
                                                "Nie znaleziono święta"));

        if (!holiday.getHolidayDate().equals(request.holidayDate())
                && holidayRepository.existsByHolidayDate(request.holidayDate())) {
            throw new BusinessValidationException("Święto w tym dniu zostało już dodane");
        }

        holidayMapper.updateFromRequest(request, holiday);
        return holidayMapper.toResponse(holidayRepository.save(holiday));
    }

    @Transactional
    public void deleteHoliday(Integer holidayId) {
        if (!holidayRepository.existsById(holidayId)) {
            throw new pl.edu.ur.teachly.common.exception.ResourceNotFoundException(
                    "Nie znaleziono święta");
        }
        holidayRepository.deleteById(holidayId);
    }
}
