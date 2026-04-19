package pl.edu.ur.teachly.tutor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.mapper.TutorMapper;
import pl.edu.ur.teachly.tutor.mapper.TutorSubjectMapper;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.repository.TutorSubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorService {
    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;
    private final TutorSubjectRepository tutorSubjectRepository;
    private final TutorSubjectMapper tutorSubjectMapper;

    @Transactional(readOnly = true)
    public List<TutorResponse> getAllTutors() {
        return tutorRepository.findAll().stream()
                .map(tutorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorResponse getTutorById(Integer tutorId) {
        return tutorRepository.findById(tutorId)
                .map(tutorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono szukanego korepetytora"));
    }

    @Transactional(readOnly = true)
    public List<TutorSubjectResponse> getTutorSubjects(Integer tutorId) {
        tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono szukanego korepetytora"));
        return tutorSubjectRepository.findByTutor_UserId(tutorId).stream()
                .map(tutorSubjectMapper::toResponse)
                .toList();
    }
}
