package pl.edu.ur.teachly.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.entity.Holiday;
import pl.edu.ur.teachly.holiday.mapper.HolidayMapper;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidayService - testy jednostkowe")
class HolidayServiceTest {

    @Mock private HolidayRepository holidayRepository;
    @Mock private HolidayMapper holidayMapper;

    @InjectMocks private HolidayService holidayService;

    @Test
    @DisplayName("getAllHolidays - zwraca listę świąt")
    void getAllHolidays_returnsList() {
        Holiday h1 = new Holiday();
        HolidayResponse r1 = new HolidayResponse(1, LocalDate.now(), "Boże Narodzenie");

        when(holidayRepository.findAll()).thenReturn(List.of(h1));
        when(holidayMapper.toResponse(h1)).thenReturn(r1);

        List<HolidayResponse> result = holidayService.getAllHolidays();

        assertThat(result).containsExactly(r1);
    }

    @Test
    @DisplayName("addHoliday - sukces")
    void addHoliday_success() {
        HolidayRequest req = new HolidayRequest(LocalDate.now(), "Nowy Rok");
        Holiday holiday = new Holiday();
        HolidayResponse response = new HolidayResponse(1, LocalDate.now(), "Nowy Rok");

        when(holidayRepository.existsByHolidayDate(req.holidayDate())).thenReturn(false);
        when(holidayMapper.toEntity(req)).thenReturn(holiday);
        when(holidayRepository.save(holiday)).thenReturn(holiday);
        when(holidayMapper.toResponse(holiday)).thenReturn(response);

        HolidayResponse result = holidayService.addHoliday(req);

        assertThat(result).isEqualTo(response);
        verify(holidayRepository).save(holiday);
    }

    @Test
    @DisplayName("addHoliday - błąd: data już istnieje")
    void addHoliday_alreadyExists_throwsException() {
        HolidayRequest req = new HolidayRequest(LocalDate.now(), "Nowy Rok");
        when(holidayRepository.existsByHolidayDate(req.holidayDate())).thenReturn(true);

        assertThatThrownBy(() -> holidayService.addHoliday(req))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    @DisplayName("updateHoliday - sukces")
    void updateHoliday_success() {
        HolidayRequest req = new HolidayRequest(LocalDate.now(), "Zmieniona nazwa");
        Holiday holiday = new Holiday();
        holiday.setHolidayDate(LocalDate.now().minusDays(1)); // stara data

        when(holidayRepository.findById(1)).thenReturn(Optional.of(holiday));
        when(holidayRepository.existsByHolidayDate(req.holidayDate())).thenReturn(false);
        when(holidayRepository.save(holiday)).thenReturn(holiday);
        when(holidayMapper.toResponse(holiday))
                .thenReturn(new HolidayResponse(1, LocalDate.now(), "Zmieniona nazwa"));

        holidayService.updateHoliday(1, req);

        verify(holidayMapper).updateFromRequest(req, holiday);
        verify(holidayRepository).save(holiday);
    }

    @Test
    @DisplayName("deleteHoliday - sukces")
    void deleteHoliday_success() {
        when(holidayRepository.existsById(1)).thenReturn(true);

        holidayService.deleteHoliday(1);

        verify(holidayRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteHoliday - błąd: nie istnieje")
    void deleteHoliday_notFound_throwsException() {
        when(holidayRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> holidayService.deleteHoliday(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
