package pl.edu.ur.teachly.lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.mapper.LessonMapper;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public LessonResponse createLesson(Integer studentId, LessonRequest request) {
        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono wybranego ucznia"));
        var tutor = tutorRepository.findById(request.tutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono takiego korepetytora"));
        var subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setStudent(student);
        lesson.setTutor(tutor);
        lesson.setSubject(subject);

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getStudentLessons(Integer studentId) {
        return lessonRepository.findByStudent_Id(studentId)
                .stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getTutorLessons(Integer tutorId) {
        return lessonRepository.findByTutor_UserId(tutorId)
                .stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Transactional
    public LessonResponse changeLessonStatus(Integer lessonId, LessonStatusRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono lekcji o podanym id"));

        lesson.setLessonStatus(request.lessonStatus());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }
}
