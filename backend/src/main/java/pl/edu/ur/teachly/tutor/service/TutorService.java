package pl.edu.ur.teachly.tutor.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSelfProfileRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSubjectRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
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

    @Transactional(readOnly = true)
    public List<TutorResponse> getAllTutors() {
        return tutorRepository.findAll().stream().map(tutorMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TutorResponse getTutorById(Integer tutorId) {
        return tutorRepository
                .findById(tutorId)
                .map(tutorMapper::toResponse)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Nie znaleziono szukanego korepetytora"));
    }

    @Transactional(readOnly = true)
    public List<TutorSubjectResponse> getTutorSubjects(Integer tutorId) {
        if (!tutorRepository.existsById(tutorId)) {
            throw new ResourceNotFoundException("Nie znaleziono szukanego korepetytora");
        }
        return tutorSubjectRepository.findByTutor_UserId(tutorId).stream()
                .map(tutorSubjectMapper::toResponse)
                .toList();
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
        var tutor =
                tutorRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono profilu korepetytora"));
        tutor.setBio(request.bio());
        tutor.setHourlyRate(request.hourlyRate());
        tutor.setOffersOnline(request.offersOnline());
        tutor.setOffersInPerson(request.offersInPerson());
        return tutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional
    public TutorSubjectResponse addMySubject(TutorSubjectRequest request, User currentUser) {
        var tutor =
                tutorRepository
                        .findById(currentUser.getId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono profilu korepetytora"));
        var subject =
                subjectRepository
                        .findById(request.subjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));
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
                        .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));
        if (!tutorSubject.getTutor().getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Brak uprawnień do usunięcia tego przedmiotu");
        }
        tutorSubjectRepository.delete(tutorSubject);
    }
}
