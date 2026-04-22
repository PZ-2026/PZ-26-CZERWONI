package pl.edu.ur.teachly.tutor.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class TutorAvailabilityService {
    private final TutorAvailabilityRecurringRepository recurringRepository;
    private final TutorAvailabilityOverrideRepository overrideRepository;
    private final TutorRepository tutorRepository;
    private final TutorAvailabilityMapper mapper;

    @Transactional(readOnly = true)
    public List<TutorAvailabilityRecurringResponse> getRecurringByTutor(Integer tutorId) {
        return recurringRepository.findByTutor_UserId(tutorId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public TutorAvailabilityRecurringResponse addRecurring(
            Integer tutorId, TutorAvailabilityRecurringRequest request) {
        Tutor tutor =
                tutorRepository
                        .findById(tutorId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego korepetytora"));
        TutorAvailabilityRecurring entity = mapper.toEntity(request);
        entity.setTutor(tutor);
        return mapper.toResponse(recurringRepository.save(entity));
    }

    @Transactional
    public void deleteRecurring(Integer id) {
        if (!recurringRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono wpisu cyklicznego");
        }
        recurringRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TutorAvailabilityOverrideResponse> getOverridesByTutor(Integer tutorId) {
        return overrideRepository.findByTutor_UserId(tutorId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public TutorAvailabilityOverrideResponse addOverride(
            Integer tutorId, TutorAvailabilityOverrideRequest request) {
        Tutor tutor =
                tutorRepository
                        .findById(tutorId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego korepetytora"));
        TutorAvailabilityOverride entity = mapper.toEntity(request);
        entity.setTutor(tutor);
        return mapper.toResponse(overrideRepository.save(entity));
    }

    @Transactional
    public void deleteOverride(Integer id) {
        if (!overrideRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono nadpisania dostępności");
        }
        overrideRepository.deleteById(id);
    }
}
