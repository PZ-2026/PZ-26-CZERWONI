package pl.edu.ur.teachly.tutor.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.util.SearchQueryUtils;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSelfProfileRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSubjectRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSearchResultResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;
import pl.edu.ur.teachly.tutor.mapper.TutorMapper;
import pl.edu.ur.teachly.tutor.mapper.TutorSubjectMapper;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.repository.TutorSubjectRepository;
import pl.edu.ur.teachly.user.entity.User;

@Service
@RequiredArgsConstructor
public class TutorService {
    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final TutorSubjectRepository tutorSubjectRepository;
    private final TutorSubjectMapper tutorSubjectMapper;
    private final SubjectRepository subjectRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<TutorResponse> getAllTutors(String query) {
        return findActiveTutors(query, null).stream().map(tutorMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TutorSearchResultResponse> searchTutors(String query, String subject) {
        var tutors = findActiveTutors(query, subject);
        if (tutors.isEmpty()) {
            return List.of();
        }

        var tutorIds = tutors.stream().map(Tutor::getUserId).toList();
        var subjectsByTutorId = loadSubjectsByTutorId(tutorIds);
        var ratingStatsByTutorId = loadRatingStatsByTutorId(tutorIds);

        return tutors.stream()
                .map(
                        tutor -> {
                            var tutorId = tutor.getUserId();
                            var subjects =
                                    subjectsByTutorId
                                            .getOrDefault(tutorId, List.of())
                                            .stream()
                                            .map(tutorSubjectMapper::toResponse)
                                            .toList();
                            var stats = ratingStatsByTutorId.get(tutorId);
                            return new TutorSearchResultResponse(
                                    tutorMapper.toResponse(tutor),
                                    subjects,
                                    stats != null ? stats.averageRating() : 0.0,
                                    stats != null ? stats.reviewCount() : 0);
                        })
                .toList();
    }

    private List<Tutor> findActiveTutors(String query, String subject) {
        return tutorRepository.searchActiveTutors(
                SearchQueryUtils.toLikePattern(query), SearchQueryUtils.normalizeLower(subject));
    }

    private Map<Integer, List<TutorSubject>> loadSubjectsByTutorId(List<Integer> tutorIds) {
        var result = new HashMap<Integer, List<TutorSubject>>();
        tutorSubjectRepository.findByTutor_UserIdIn(tutorIds).stream()
                .forEach(
                        tutorSubject ->
                                result.computeIfAbsent(
                                                tutorSubject.getTutor().getUserId(),
                                                ignored -> new java.util.ArrayList<>())
                                        .add(tutorSubject));
        return result;
    }

    private Map<Integer, RatingStats> loadRatingStatsByTutorId(List<Integer> tutorIds) {
        var result = new HashMap<Integer, RatingStats>();
        for (Object[] row : reviewRepository.findRatingStatsByTutorIds(tutorIds)) {
            var tutorId = (Integer) row[0];
            var average =
                    row[1] == null
                            ? 0.0
                            : BigDecimal.valueOf(((Number) row[1]).doubleValue())
                                    .setScale(1, RoundingMode.HALF_UP)
                                    .doubleValue();
            var count = ((Number) row[2]).intValue();
            result.put(tutorId, new RatingStats(average, count));
        }
        return result;
    }

    private record RatingStats(Double averageRating, Integer reviewCount) {}

    @Transactional(readOnly = true)
    public TutorResponse getTutorById(Integer tutorId) {
        return tutorRepository
                .findById(tutorId)
                .filter(this::isTutorActive)
                .map(tutorMapper::toResponse)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Nie znaleziono szukanego korepetytora"));
    }

    @Transactional(readOnly = true)
    public List<TutorSubjectResponse> getTutorSubjects(Integer tutorId) {
        tutorRepository
                .findById(tutorId)
                .filter(this::isTutorActive)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Nie znaleziono szukanego korepetytora"));
        return tutorSubjectRepository.findByTutor_UserId(tutorId).stream()
                .map(tutorSubjectMapper::toResponse)
                .toList();
    }

    private boolean isTutorActive(Tutor tutor) {
        return tutor.getUser() != null && Boolean.TRUE.equals(tutor.getUser().getIsActive());
    }

    @Transactional
    public TutorResponse adminUpdateTutor(Integer tutorId, TutorRequest request) {
        var tutor =
                tutorRepository
                        .findById(tutorId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego korepetytora"));
        tutorMapper.updateFromRequest(request, tutor);
        return tutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional
    public TutorResponse updateMyProfile(TutorSelfProfileRequest request, User currentUser) {
        if (!Boolean.TRUE.equals(request.offersOnline())
                && !Boolean.TRUE.equals(request.offersInPerson())) {
            throw new BusinessValidationException(
                    "Wybierz co najmniej jedną formę zajęć (online lub stacjonarnie)");
        }
        if (tutorSubjectRepository.findByTutor_UserId(currentUser.getId()).isEmpty()) {
            throw new BusinessValidationException("Dodaj co najmniej jeden prowadzony przedmiot");
        }
        var tutor =
                tutorRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono profilu korepetytora"));
        tutor.setBio(request.bio());
        tutor.setHourlyRate(request.hourlyRate());
        tutor.setOffersOnline(request.offersOnline());
        tutor.setOffersInPerson(request.offersInPerson());
        return tutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional
    public TutorSubjectResponse adminAddSubject(Integer tutorId, TutorSubjectRequest request) {
        var tutor =
                tutorRepository
                        .findById(tutorId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego korepetytora"));
        return addSubject(tutor, request);
    }

    @Transactional
    public void adminRemoveSubject(Integer tutorId, Integer tutorSubjectId) {
        removeSubject(tutorId, tutorSubjectId, false);
    }

    @Transactional
    public TutorSubjectResponse addMySubject(TutorSubjectRequest request, User currentUser) {
        var tutor =
                tutorRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono profilu korepetytora"));
        return addSubject(tutor, request);
    }

    private TutorSubjectResponse addSubject(Tutor tutor, TutorSubjectRequest request) {
        if (tutorSubjectRepository.existsByTutor_UserIdAndSubject_Id(
                tutor.getUserId(), request.subjectId())) {
            throw new BusinessValidationException(
                    "Ten przedmiot jest już przypisany do korepetytora");
        }
        var subject =
                subjectRepository
                        .findById(request.subjectId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));
        var tutorSubject =
                TutorSubject.builder()
                        .tutor(tutor)
                        .subject(subject)
                        .levelPrimary(request.levelPrimary())
                        .levelHighSchool(request.levelHighSchool())
                        .levelUniversity(request.levelUniversity())
                        .levelExamPrep(request.levelExamPrep())
                        .levelProfessional(request.levelProfessional())
                        .build();
        return tutorSubjectMapper.toResponse(tutorSubjectRepository.save(tutorSubject));
    }

    @Transactional
    public void removeMySubject(Integer tutorSubjectId, User currentUser) {
        var tutorSubject =
                tutorSubjectRepository
                        .findById(tutorSubjectId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));
        if (!tutorSubject.getTutor().getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Brak uprawnień do usunięcia tego przedmiotu");
        }
        removeSubject(currentUser.getId(), tutorSubjectId, false);
    }

    private void removeSubject(
            Integer tutorId, Integer tutorSubjectId, boolean skipMinSubjectCheck) {
        var tutorSubject =
                tutorSubjectRepository
                        .findById(tutorSubjectId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));
        if (!tutorSubject.getTutor().getUserId().equals(tutorId)) {
            throw new ResourceNotFoundException("Nie znaleziono przedmiotu");
        }
        if (!skipMinSubjectCheck
                && tutorSubjectRepository.findByTutor_UserId(tutorId).size() <= 1) {
            throw new BusinessValidationException(
                    "Musi pozostać co najmniej jeden prowadzony przedmiot");
        }
        tutorSubjectRepository.delete(tutorSubject);
    }
}
