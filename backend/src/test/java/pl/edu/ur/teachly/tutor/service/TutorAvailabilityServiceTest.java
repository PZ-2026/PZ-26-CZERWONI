package pl.edu.ur.teachly.tutor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityOverrideRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityRecurringRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityOverrideResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityRecurringResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;
import pl.edu.ur.teachly.tutor.mapper.TutorAvailabilityMapper;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityOverrideRepository;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityRecurringRepository;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorAvailabilityService - testy jednostkowe")
class TutorAvailabilityServiceTest {

    @Mock private TutorAvailabilityRecurringRepository recurringRepository;
    @Mock private TutorAvailabilityOverrideRepository overrideRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private TutorAvailabilityMapper mapper;

    @InjectMocks
    private TutorAvailabilityService availabilityService;

    @Test
    @DisplayName("getRecurringByTutor - zwraca listę cyklicznej dostępności")
    void getRecurringByTutor_returnsList() {
        TutorAvailabilityRecurring entity = new TutorAvailabilityRecurring();
        TutorAvailabilityRecurringResponse response = new TutorAvailabilityRecurringResponse(1, 1, 1, LocalTime.MIN, LocalTime.MAX, null);

        when(recurringRepository.findByTutor_UserId(1)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<TutorAvailabilityRecurringResponse> result = availabilityService.getRecurringByTutor(1);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("addRecurring - sukces")
    void addRecurring_success() {
        TutorAvailabilityRecurringRequest req = new TutorAvailabilityRecurringRequest(1, LocalTime.MIN, LocalTime.MAX, null);
        Tutor tutor = new Tutor();
        TutorAvailabilityRecurring entity = new TutorAvailabilityRecurring();
        TutorAvailabilityRecurringResponse response = new TutorAvailabilityRecurringResponse(1, 1, 1, LocalTime.MIN, LocalTime.MAX, null);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(mapper.toEntity(req)).thenReturn(entity);
        when(recurringRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        TutorAvailabilityRecurringResponse result = availabilityService.addRecurring(1, req);

        assertThat(result).isEqualTo(response);
        verify(recurringRepository).save(entity);
    }

    @Test
    @DisplayName("deleteRecurring - sukces")
    void deleteRecurring_success() {
        when(recurringRepository.existsById(1)).thenReturn(true);

        availabilityService.deleteRecurring(1);

        verify(recurringRepository).deleteById(1);
    }

    @Test
    @DisplayName("getOverridesByTutor - zwraca listę nadpisań")
    void getOverridesByTutor_returnsList() {
        TutorAvailabilityOverride entity = new TutorAvailabilityOverride();
        TutorAvailabilityOverrideResponse response = new TutorAvailabilityOverrideResponse(1, 1, LocalDate.now(), LocalTime.MIN, LocalTime.MAX);

        when(overrideRepository.findByTutor_UserId(1)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<TutorAvailabilityOverrideResponse> result = availabilityService.getOverridesByTutor(1);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("addOverride - sukces")
    void addOverride_success() {
        TutorAvailabilityOverrideRequest req = new TutorAvailabilityOverrideRequest(LocalDate.now(), LocalTime.MIN, LocalTime.MAX);
        Tutor tutor = new Tutor();
        TutorAvailabilityOverride entity = new TutorAvailabilityOverride();
        TutorAvailabilityOverrideResponse response = new TutorAvailabilityOverrideResponse(1, 1, LocalDate.now(), LocalTime.MIN, LocalTime.MAX);

        when(tutorRepository.findById(1)).thenReturn(Optional.of(tutor));
        when(mapper.toEntity(req)).thenReturn(entity);
        when(overrideRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        TutorAvailabilityOverrideResponse result = availabilityService.addOverride(1, req);

        assertThat(result).isEqualTo(response);
        verify(overrideRepository).save(entity);
    }

    @Test
    @DisplayName("deleteOverride - sukces")
    void deleteOverride_success() {
        when(overrideRepository.existsById(1)).thenReturn(true);

        availabilityService.deleteOverride(1);

        verify(overrideRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteOverride - błąd: nie istnieje")
    void deleteOverride_notFound_throwsException() {
        when(overrideRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> availabilityService.deleteOverride(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
