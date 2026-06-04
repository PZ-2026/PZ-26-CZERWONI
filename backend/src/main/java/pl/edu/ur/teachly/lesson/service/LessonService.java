package pl.edu.ur.teachly.lesson.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.exception.SlotNotAvailableException;
import pl.edu.ur.teachly.lesson.dto.request.AdminLessonUpdateRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.PaymentStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.StudentNotesRequest;
import pl.edu.ur.teachly.lesson.dto.request.TutorNotesRequest;
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
        var student =
                userRepository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono wybranego ucznia"));
        var tutor =
                tutorRepository
                        .findById(request.tutorId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono takiego korepetytora"));
        if (tutor.getUser() == null || !Boolean.TRUE.equals(tutor.getUser().getIsActive())) {
            throw new BusinessValidationException("Korepetytor jest niedostępny");
        }
        var subject =
                subjectRepository
                        .findById(request.subjectId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono przedmiotu"));

        long duration = Duration.between(request.timeFrom(), request.timeTo()).toMinutes();

        if (duration <= 0 || duration % 30 != 0) {
            throw new IllegalArgumentException("Niepoprawny zakres czasu");
        }

        if (LocalDateTime.of(request.lessonDate(), request.timeFrom())
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nie można zarezerwować lekcji w przeszłości");
        }

        List<TimetableDayResponse> available =
                timetableService.getTimetable(
                        request.tutorId(), request.lessonDate(), request.lessonDate(), studentId);

        boolean allAvailable =
                available.stream()
                        .flatMap(
                                day ->
                                        day.getAvailableSlots() == null
                                                ? Stream.empty()
                                                : day.getAvailableSlots().stream())
                        .anyMatch(
                                slot ->
                                        !slot.getTimeFrom().isAfter(request.timeFrom())
                                                && !slot.getTimeTo().isBefore(request.timeTo()));
        if (!allAvailable) {
            throw new SlotNotAvailableException("Wybrany termin jest niedostępny");
        }

        boolean tutorConflict =
                lessonRepository.existsConflictingLesson(
                        request.tutorId(),
                        request.lessonDate(),
                        request.timeFrom(),
                        request.timeTo(),
                        LessonStatus.CONFIRMED);
        if (tutorConflict) {
            throw new SlotNotAvailableException(
                    "Korepetytor ma już zarezerwowaną lekcję w tym czasie");
        }

        boolean studentConflict =
                lessonRepository.existsConflictingStudentLesson(
                        studentId,
                        request.lessonDate(),
                        request.timeFrom(),
                        request.timeTo(),
                        LessonStatus.CANCELLED);
        if (studentConflict) {
            throw new SlotNotAvailableException("Masz już zarezerwowaną lekcję w tym czasie");
        }

        BigDecimal amount =
                tutor.getHourlyRate()
                        .multiply(BigDecimal.valueOf(duration))
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setStudent(student);
        lesson.setTutor(tutor);
        lesson.setSubject(subject);
        lesson.setLessonStatus(LessonStatus.PENDING);
        lesson.setPaymentStatus(PaymentStatus.PENDING);
        lesson.setAmount(amount);

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public LessonResponse getLesson(Integer lessonId) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));
        User caller = getCurrentUser();
        if (caller.getUserRole() != UserRole.ADMIN) {
            boolean isParticipant =
                    (lesson.getStudent() != null
                                    && lesson.getStudent().getId().equals(caller.getId()))
                            || (lesson.getTutor() != null
                                    && lesson.getTutor().getUserId().equals(caller.getId()));
            if (!isParticipant) {
                throw new AccessDeniedException("Brak dostępu do tej lekcji");
            }
        }
        return lessonMapper.toResponse(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream().map(lessonMapper::toResponse).toList();
    }

    @Transactional
    public LessonResponse adminUpdateLesson(Integer lessonId, AdminLessonUpdateRequest request) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));
        lesson.setLessonDate(request.lessonDate());
        lesson.setTimeFrom(request.timeFrom());
        lesson.setTimeTo(request.timeTo());
        lesson.setFormat(request.format());
        lesson.setLessonStatus(request.lessonStatus());
        lesson.setPaymentStatus(request.paymentStatus());
        lesson.setAmount(request.amount());
        lesson.setStudentNotes(request.studentNotes());
        lesson.setTutorNotes(request.tutorNotes());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getStudentLessons(Integer studentId) {
        return lessonRepository.findByStudent_Id(studentId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getTutorLessons(Integer tutorId) {
        return lessonRepository.findByTutor_UserId(tutorId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Transactional
    public LessonResponse changeLessonStatus(Integer lessonId, LessonStatusRequest request) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));

        LessonStatus newStatus = request.lessonStatus();
        LessonStatus currentStatus = lesson.getLessonStatus();
        User currentUser = getCurrentUser();
        UserRole currentUserRole = currentUser.getUserRole();

        if (currentUserRole != UserRole.ADMIN) {
            boolean isParticipant =
                    (lesson.getStudent() != null
                                    && lesson.getStudent().getId().equals(currentUser.getId()))
                            || (lesson.getTutor() != null
                                    && lesson.getTutor().getUserId().equals(currentUser.getId()));
            if (!isParticipant) {
                throw new AccessDeniedException("Brak dostępu do tej lekcji");
            }

            LocalDateTime lessonStart =
                    LocalDateTime.of(lesson.getLessonDate(), lesson.getTimeFrom());

            if (!isValidTransition(currentStatus, newStatus, currentUserRole, lessonStart)) {
                throw new IllegalStateException("Nie można zmienić statusu lekcji na wybrany");
            }
        }

        lesson.setLessonStatus(newStatus);
        if (request.tutorNotes() != null) {
            lesson.setTutorNotes(request.tutorNotes());
        }
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse updateStudentNotes(
            Integer lessonId, StudentNotesRequest request, Integer callerId) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));
        if (!lesson.getStudent().getId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do edycji notatek tej lekcji");
        }
        lesson.setStudentNotes(request.studentNotes());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse updateTutorNotes(
            Integer lessonId, TutorNotesRequest request, Integer callerId) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));
        if (!lesson.getTutor().getUserId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do edycji notatek tej lekcji");
        }
        lesson.setTutorNotes(request.tutorNotes());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse updatePaymentStatus(Integer lessonId, PaymentStatusRequest request) {
        Lesson lesson =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej lekcji"));
        lesson.setPaymentStatus(request.paymentStatus());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Brak uwierzytelnienia");
        }
        return user;
    }

    private boolean isValidTransition(
            LessonStatus current, LessonStatus next, UserRole userRole, LocalDateTime lessonStart) {
        if (current == next) {
            return false;
        }

        if (next == LessonStatus.COMPLETED && userRole != UserRole.TUTOR) {
            return false;
        }

        if (next == LessonStatus.COMPLETED
                && LocalDateTime.now().isBefore(lessonStart.plusMinutes(30))) {
            throw new IllegalStateException(
                    "Lekcja może zostać oznaczona jako zakończona dopiero po upływie 30 minut od rozpoczęcia");
        }

        if (current == LessonStatus.PENDING
                && next == LessonStatus.CONFIRMED
                && userRole != UserRole.TUTOR) {
            return false;
        }

        return switch (current) {
            case PENDING -> next == LessonStatus.CONFIRMED || next == LessonStatus.CANCELLED;
            case CONFIRMED -> next == LessonStatus.CANCELLED || next == LessonStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
