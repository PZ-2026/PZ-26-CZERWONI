package pl.edu.ur.teachly.holiday.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.entity.Holiday;
import pl.edu.ur.teachly.holiday.mapper.HolidayMapper;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;

/**
 * Serwis zarządzający dniami wolnymi (świętami) w kalendarzu systemu.
 *
 * <p>Dni wolne są uwzględniane przy generowaniu planu zajęć korepetytorów — w tych dniach terminy
 * są automatycznie blokowane.
 */
@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    /**
     * Zwraca listę wszystkich zdefiniowanych dni wolnych.
     *
     * @return lista dni wolnych
     */
    @Transactional(readOnly = true)
    public List<HolidayResponse> getAllHolidays() {
        return holidayRepository.findAll().stream().map(holidayMapper::toResponse).toList();
    }

    /**
     * Dodaje nowy dzień wolny. Data musi być unikalna w systemie.
     *
     * @param request dane nowego dnia wolnego
     * @return zapisany dzień wolny
     * @throws BusinessValidationException gdy dzień wolny w tej dacie już istnieje
     */
    @Transactional
    public HolidayResponse addHoliday(HolidayRequest request) {
        if (holidayRepository.existsByHolidayDate(request.holidayDate())) {
            throw new BusinessValidationException("Święto w tym dniu zostało już dodane");
        }
        Holiday holiday = holidayMapper.toEntity(request);
        return holidayMapper.toResponse(holidayRepository.save(holiday));
    }

    /**
     * Aktualizuje istniejący dzień wolny. Jeśli data ulega zmianie, sprawdzana jest jej unikalność.
     *
     * @param holidayId identyfikator dnia wolnego
     * @param request nowe dane dnia wolnego
     * @return zaktualizowany dzień wolny
     * @throws ResourceNotFoundException gdy dzień wolny nie istnieje
     * @throws BusinessValidationException gdy nowa data jest już zajęta
     */
    @Transactional
    public HolidayResponse updateHoliday(Integer holidayId, HolidayRequest request) {
        Holiday holiday =
                holidayRepository
                        .findById(holidayId)
                        .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono święta"));

        if (!holiday.getHolidayDate().equals(request.holidayDate())
                && holidayRepository.existsByHolidayDate(request.holidayDate())) {
            throw new BusinessValidationException("Święto w tym dniu zostało już dodane");
        }

        holidayMapper.updateFromRequest(request, holiday);
        return holidayMapper.toResponse(holidayRepository.save(holiday));
    }

    /**
     * Usuwa dzień wolny o podanym identyfikatorze.
     *
     * @param holidayId identyfikator dnia wolnego
     * @throws ResourceNotFoundException gdy dzień wolny nie istnieje
     */
    @Transactional
    public void deleteHoliday(Integer holidayId) {
        if (!holidayRepository.existsById(holidayId)) {
            throw new ResourceNotFoundException("Nie znaleziono święta");
        }
        holidayRepository.deleteById(holidayId);
    }
}
