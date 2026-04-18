package pl.edu.ur.teachly.lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.exception.SlotNotAvailableException;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.mapper.LessonMapper;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.service.TimetableService;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;
    private final TimetableService timetableService;

    @Transactional
    public LessonResponse createLesson(Integer studentId, LessonRequest request) {
        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono wybranego ucznia"));
        var tutor = tutorRepository.findById(request.tutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono takiego korepetytora"));
        var subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));

        long duration = Duration.between(request.timeFrom(), request.timeTo()).toMinutes();

        if (duration <= 0 || duration % 30 != 0) {
            throw new IllegalArgumentException("Niepoprawny zakres czasu");
        }

        List<TimetableDayResponse> available = timetableService.getTimetable(
                request.tutorId(),
                request.lessonDate(),
                request.lessonDate(),
                studentId
        );

        boolean allAvailable = available.stream()
                .flatMap(day -> day.getAvailableSlots() == null
                        ? Stream.empty()
                        : day.getAvailableSlots().stream())
                .anyMatch(slot -> !slot.getTimeFrom().isAfter(request.timeFrom())
                        && !slot.getTimeTo().isBefore(request.timeTo()));
        if (!allAvailable) {
            throw new SlotNotAvailableException("Wybrany termin jest niedostępny");
        }

        boolean conflict = lessonRepository.existsConflictingLesson(
                request.tutorId(),
                request.lessonDate(),
                request.timeFrom(),
                request.timeTo(),
                LessonStatus.CONFIRMED
        );
        if (conflict) {
            throw new SlotNotAvailableException("Korepetytor ma już zarezerwowaną lekcję w tym czasie");
        }

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setStudent(student);
        lesson.setTutor(tutor);
        lesson.setSubject(subject);
        lesson.setLessonStatus(LessonStatus.PENDING);
        lesson.setPaymentStatus(PaymentStatus.PENDING);

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
    public LessonResponse changeLessonStatus(Integer lessonId, LessonStatusRequest request, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono lekcji o podanym id"));

        LessonStatus newStatus = request.lessonStatus();
        LessonStatus currentStatus = lesson.getLessonStatus();

        if (currentUser.getUserRole() != UserRole.ADMIN) {
            LocalDateTime lessonStart = LocalDateTime.of(lesson.getLessonDate(), lesson.getTimeFrom());

            if (!isValidTransition(currentStatus, newStatus, currentUser.getUserRole(), lessonStart)) {
                throw new IllegalStateException("Nie można zmienić statusu lekcji z " + currentStatus + " na " + newStatus);
            }
        }

        lesson.setLessonStatus(newStatus);
        if (request.tutorNotes() != null) {
            lesson.setTutorNotes(request.tutorNotes());
        }
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    private boolean isValidTransition(LessonStatus current, LessonStatus next, UserRole userRole, LocalDateTime lessonStart) {
        if (current == next)
            return false;

        if (next == LessonStatus.COMPLETED && userRole != UserRole.TUTOR)
            return false;

        if (next == LessonStatus.COMPLETED && LocalDateTime.now().isBefore(lessonStart.plusMinutes(30))) {
            throw new IllegalStateException(
                    "Lekcja może zostać oznaczona jako zakończona dopiero po upływie 30 minut od rozpoczęcia"
            );
        }

        if (current == LessonStatus.PENDING && next == LessonStatus.CONFIRMED && userRole != UserRole.TUTOR)
            return false;

        return switch (current) {
            case PENDING -> next == LessonStatus.CONFIRMED || next == LessonStatus.CANCELLED;
            case CONFIRMED -> next == LessonStatus.CANCELLED || next == LessonStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
