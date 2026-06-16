package pl.edu.ur.teachly.lesson.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.exception.SlotNotAvailableException;
import pl.edu.ur.teachly.common.util.SearchQueryUtils;
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

/**
 * Serwis zarządzający lekcjami w aplikacji Teachly.
 *
 * <p>Obsługuje tworzenie lekcji z weryfikacją dostępności terminów, pobieranie lekcji z kontrolą
 * uprawnień, zmianę statusów oraz aktualizację notatek i statusu płatności.
 */
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;
    private final TimetableService timetableService;

    /**
     * Tworzy nową lekcję po weryfikacji dostępności korepetytora i ucznia.
     *
     * <p>Metoda sprawdza: czas trwania (wielokrotność 30 minut), czy termin nie jest w przeszłości,
     * dostępność w planie zajęć korepetytora oraz brak kolidujących lekcji dla obu stron. Kwota
     * jest obliczana proporcjonalnie do stawki godzinowej korepetytora.
     *
     * @param studentId identyfikator ucznia składającego rezerwację
     * @param request dane rezerwacji lekcji
     * @return szczegóły utworzonej lekcji
     * @throws ResourceNotFoundException gdy uczeń, korepetytor lub przedmiot nie istnieje
     * @throws BusinessValidationException gdy korepetytor jest nieaktywny
     * @throws SlotNotAvailableException gdy wybrany termin jest zajęty
     */
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

    /**
     * Zwraca szczegóły lekcji. Użytkownik niebędący administratorem może odczytać wyłącznie lekcje,
     * w których uczestniczy jako uczeń lub korepetytor.
     *
     * @param lessonId identyfikator lekcji
     * @return szczegóły lekcji
     * @throws ResourceNotFoundException gdy lekcja nie istnieje
     * @throws AccessDeniedException gdy wywołujący nie jest uczestnikiem lekcji
     */
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

    /**
     * Wyszukuje lekcje według podanych filtrów. Dostępne wyłącznie dla administratora.
     *
     * @param query fraza wyszukiwania (imię/nazwisko uczestnika lub przedmiot)
     * @param status filtr statusu lekcji
     * @param paymentStatus filtr statusu płatności
     * @param format filtr formatu lekcji (online/stacjonarna)
     * @param upcoming jeśli {@code true}, zwraca tylko przyszłe lekcje
     * @return lista pasujących lekcji
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> searchLessons(
            String query,
            LessonStatus status,
            PaymentStatus paymentStatus,
            LessonFormat format,
            Boolean upcoming) {
        return lessonRepository
                .searchLessons(
                        SearchQueryUtils.toLikePattern(query),
                        status,
                        paymentStatus,
                        format,
                        upcoming,
                        LocalDate.now())
                .stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    /**
     * Aktualizuje dane lekcji przez administratora bez ograniczeń reguł biznesowych.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowe dane lekcji
     * @return zaktualizowane szczegóły lekcji
     * @throws ResourceNotFoundException gdy lekcja nie istnieje
     */
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

    /**
     * Zwraca wszystkie lekcje powiązane z danym uczniem.
     *
     * @param studentId identyfikator użytkownika o roli STUDENT
     * @return lista lekcji ucznia
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> getStudentLessons(Integer studentId) {
        return lessonRepository.findByStudent_Id(studentId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    /**
     * Zwraca wszystkie lekcje powiązane z danym korepetytorem.
     *
     * @param tutorId identyfikator użytkownika o roli TUTOR
     * @return lista lekcji korepetytora
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> getTutorLessons(Integer tutorId) {
        return lessonRepository.findByTutor_UserId(tutorId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    /**
     * Zmienia status lekcji zgodnie z dozwolonymi przejściami stanów.
     *
     * <p>Dla użytkowników niebędących administratorami sprawdzane jest uczestnictwo w lekcji oraz
     * poprawność przejścia między stanami. Oznaczenie lekcji jako zakończonej jest możliwe dopiero
     * 30 minut po jej planowym rozpoczęciu.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowy status wraz z opcjonalnymi notatkami korepetytora
     * @return zaktualizowane szczegóły lekcji
     * @throws ResourceNotFoundException gdy lekcja nie istnieje
     * @throws AccessDeniedException gdy wywołujący nie jest uczestnikiem lekcji
     * @throws IllegalStateException gdy przejście między stanami jest niedozwolone
     * @throws SlotNotAvailableException gdy zatwierdzany termin koliduje z inną potwierdzoną lekcją
     *     korepetytora
     */
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

        if (currentStatus == LessonStatus.PENDING && newStatus == LessonStatus.CONFIRMED) {
            boolean slotTaken =
                    lessonRepository.existsConflictingLesson(
                            lesson.getTutor().getUserId(),
                            lesson.getLessonDate(),
                            lesson.getTimeFrom(),
                            lesson.getTimeTo(),
                            LessonStatus.CONFIRMED);
            if (slotTaken) {
                throw new SlotNotAvailableException(
                        "Korepetytor ma już potwierdzoną lekcję w tym czasie");
            }
        }

        lesson.setLessonStatus(newStatus);
        if (request.tutorNotes() != null
                && (currentUserRole == UserRole.TUTOR || currentUserRole == UserRole.ADMIN)) {
            lesson.setTutorNotes(request.tutorNotes());
        }
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    /**
     * Aktualizuje notatki ucznia dla wskazanej lekcji.
     *
     * @param lessonId identyfikator lekcji
     * @param request treść notatek
     * @param callerId identyfikator wywołującego użytkownika
     * @return zaktualizowane szczegóły lekcji
     * @throws AccessDeniedException gdy wywołujący nie jest uczniem tej lekcji
     */
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
        if (lesson.getStudent() == null || !lesson.getStudent().getId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do edycji notatek tej lekcji");
        }
        lesson.setStudentNotes(request.studentNotes());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    /**
     * Aktualizuje notatki korepetytora dla wskazanej lekcji.
     *
     * @param lessonId identyfikator lekcji
     * @param request treść notatek
     * @param callerId identyfikator wywołującego użytkownika
     * @return zaktualizowane szczegóły lekcji
     * @throws AccessDeniedException gdy wywołujący nie jest korepetytorem tej lekcji
     */
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
        if (lesson.getTutor() == null || !lesson.getTutor().getUserId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do edycji notatek tej lekcji");
        }
        lesson.setTutorNotes(request.tutorNotes());
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    /**
     * Aktualizuje status płatności lekcji. Dostępne wyłącznie dla administratora.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowy status płatności
     * @return zaktualizowane szczegóły lekcji
     */
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

    /**
     * Zwraca aktualnie uwierzytelnionego użytkownika z kontekstu bezpieczeństwa.
     *
     * @return zalogowany użytkownik
     * @throws IllegalStateException gdy brak aktywnego uwierzytelnienia
     */
    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Brak uwierzytelnienia");
        }
        return user;
    }

    /**
     * Sprawdza, czy przejście między statusami lekcji jest dozwolone dla danej roli.
     *
     * <p>Reguły przejść:
     *
     * <ul>
     *   <li>PENDING → CONFIRMED lub CANCELLED (tylko TUTOR może potwierdzić)
     *   <li>CONFIRMED → CANCELLED lub COMPLETED
     *   <li>COMPLETED/CANCELLED → brak dozwolonych przejść
     *   <li>COMPLETED jest możliwe dopiero 30 minut po rozpoczęciu lekcji
     * </ul>
     *
     * @param current aktualny status lekcji
     * @param next docelowy status lekcji
     * @param userRole rola wywołującego użytkownika
     * @param lessonStart data i godzina rozpoczęcia lekcji
     * @return {@code true} jeśli przejście jest dozwolone
     */
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
