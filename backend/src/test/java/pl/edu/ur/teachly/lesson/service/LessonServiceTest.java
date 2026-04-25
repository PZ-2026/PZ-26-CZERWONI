package pl.edu.ur.teachly.lesson.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.LessonFormat;
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
import pl.edu.ur.teachly.subject.entity.Subject;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.tutor.dto.response.TimeSlot;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.tutor.service.TimetableService;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonService – testy jednostkowe")
class LessonServiceTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private LessonMapper lessonMapper;
    @Mock private UserRepository userRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private TimetableService timetableService;

    @InjectMocks
    private LessonService lessonService;

    // ─── helpers ─────────────────────────────────────────────────────────────

    private User student(int id) {
        return User.builder().id(id).userRole(UserRole.STUDENT).isActive(true).build();
    }

    private User tutorUser(int id) {
        return User.builder().id(id).userRole(UserRole.TUTOR).isActive(true).build();
    }

    private Tutor tutor(int id, User user) {
        return Tutor.builder().userId(id).user(user).hourlyRate(BigDecimal.valueOf(50)).build();
    }

    private LessonRequest validRequest() {
        return new LessonRequest(
                2,
                1,
                LocalDate.of(2025, 6, 10),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LessonFormat.ONLINE,
                LessonStatus.PENDING,
                null,
                BigDecimal.valueOf(50)
        );
    }

    private TimetableDayResponse availableDay(LocalDate date, LocalTime from, LocalTime to) {
        return new TimetableDayResponse(date, List.of(new TimeSlot(from, to)));
    }

    // ─── createLesson ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createLesson")
    class CreateLesson {

        @Test
        @DisplayName("sukces: lekcja zostaje zapisana i zwrócona")
        void createLesson_success() {
            User s = student(1);
            User tutorUsr = tutorUser(2);
            Tutor t = tutor(2, tutorUsr);
            Subject sub = Subject.builder().id(1).subjectName("Matematyka").build();
            LessonRequest req = validRequest();
            Lesson lesson = new Lesson();
            LessonResponse expectedResponse = new LessonResponse(
                    10, 2, "Adam", "Nowak", 1, "Jan", "Kowalski",
                    1, "Matematyka",
                    req.lessonDate(), req.timeFrom(), req.timeTo(),
                    LessonFormat.ONLINE, LessonStatus.PENDING,
                    null, null, BigDecimal.valueOf(50), PaymentStatus.PENDING, null, null
            );

            when(userRepository.findById(1)).thenReturn(Optional.of(s));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(t));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(sub));
            when(timetableService.getTimetable(eq(2), any(), any(), eq(1)))
                    .thenReturn(List.of(availableDay(req.lessonDate(), LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(lessonRepository.existsConflictingLesson(any(), any(), any(), any(), any())).thenReturn(false);
            when(lessonRepository.existsConflictingStudentLesson(any(), any(), any(), any(), any())).thenReturn(false);
            when(lessonMapper.toEntity(req)).thenReturn(lesson);
            when(lessonRepository.save(lesson)).thenReturn(lesson);
            when(lessonMapper.toResponse(lesson)).thenReturn(expectedResponse);

            LessonResponse result = lessonService.createLesson(1, req);

            assertThat(result).isEqualTo(expectedResponse);
            verify(lessonRepository).save(lesson);
        }

        @Test
        @DisplayName("błąd: student nie istnieje")
        void createLesson_studentNotFound() {
            when(userRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.createLesson(99, validRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("ucznia");
        }

        @Test
        @DisplayName("błąd: korepetytor nie istnieje")
        void createLesson_tutorNotFound() {
            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.createLesson(1, validRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("korepetytora");
        }

        @Test
        @DisplayName("błąd: przedmiot nie istnieje")
        void createLesson_subjectNotFound() {
            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.createLesson(1, validRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("przedmiotu");
        }

        @Test
        @DisplayName("błąd: czas trwania nie jest wielokrotnością 30 minut")
        void createLesson_invalidDuration_notMultipleOf30() {
            LessonRequest req = new LessonRequest(
                    2, 1, LocalDate.of(2025, 6, 10),
                    LocalTime.of(10, 0), LocalTime.of(10, 45),
                    LessonFormat.ONLINE, LessonStatus.PENDING, null, BigDecimal.valueOf(50)
            );

            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(Subject.builder().id(1).build()));

            assertThatThrownBy(() -> lessonService.createLesson(1, req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("czas");
        }

        @Test
        @DisplayName("błąd: czas od = czas do (zerowy czas trwania)")
        void createLesson_zeroDuration_throws() {
            LessonRequest req = new LessonRequest(
                    2, 1, LocalDate.of(2025, 6, 10),
                    LocalTime.of(10, 0), LocalTime.of(10, 0),
                    LessonFormat.ONLINE, LessonStatus.PENDING, null, BigDecimal.valueOf(50)
            );

            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(Subject.builder().id(1).build()));

            assertThatThrownBy(() -> lessonService.createLesson(1, req))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("błąd: slot niedostępny (korepetytor nie ma wolnego czasu)")
        void createLesson_slotNotAvailable() {
            LessonRequest req = validRequest();

            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(Subject.builder().id(1).build()));
            when(timetableService.getTimetable(any(), any(), any(), any()))
                    .thenReturn(List.of(new TimetableDayResponse(req.lessonDate(), List.of())));

            assertThatThrownBy(() -> lessonService.createLesson(1, req))
                    .isInstanceOf(SlotNotAvailableException.class)
                    .hasMessageContaining("niedostępny");
        }

        @Test
        @DisplayName("błąd: konflikt z istniejącą lekcją tutora")
        void createLesson_tutorConflict() {
            LessonRequest req = validRequest();

            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(Subject.builder().id(1).build()));
            when(timetableService.getTimetable(any(), any(), any(), any()))
                    .thenReturn(List.of(availableDay(req.lessonDate(), LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(lessonRepository.existsConflictingLesson(any(), any(), any(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> lessonService.createLesson(1, req))
                    .isInstanceOf(SlotNotAvailableException.class)
                    .hasMessageContaining("Korepetytor");
        }

        @Test
        @DisplayName("błąd: student ma już inną lekcję w tym czasie")
        void createLesson_studentConflict() {
            LessonRequest req = validRequest();

            when(userRepository.findById(1)).thenReturn(Optional.of(student(1)));
            when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor(2, tutorUser(2))));
            when(subjectRepository.findById(1)).thenReturn(Optional.of(Subject.builder().id(1).build()));
            when(timetableService.getTimetable(any(), any(), any(), any()))
                    .thenReturn(List.of(availableDay(req.lessonDate(), LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(lessonRepository.existsConflictingLesson(any(), any(), any(), any(), any())).thenReturn(false);
            when(lessonRepository.existsConflictingStudentLesson(any(), any(), any(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> lessonService.createLesson(1, req))
                    .isInstanceOf(SlotNotAvailableException.class)
                    .hasMessageContaining("zarezerwowaną");
        }
    }

    // ─── getStudentLessons / getTutorLessons ──────────────────────────────────

    @Test
    @DisplayName("getStudentLessons – zwraca lekcje studenta")
    void getStudentLessons_returnsLessons() {
        Lesson l = new Lesson();
        LessonResponse lr = mock(LessonResponse.class);
        when(lessonRepository.findByStudent_Id(1)).thenReturn(List.of(l));
        when(lessonMapper.toResponse(l)).thenReturn(lr);

        List<LessonResponse> result = lessonService.getStudentLessons(1);

        assertThat(result).containsExactly(lr);
    }

    @Test
    @DisplayName("getTutorLessons – zwraca lekcje korepetytora")
    void getTutorLessons_returnsLessons() {
        Lesson l = new Lesson();
        LessonResponse lr = mock(LessonResponse.class);
        when(lessonRepository.findByTutor_UserId(2)).thenReturn(List.of(l));
        when(lessonMapper.toResponse(l)).thenReturn(lr);

        List<LessonResponse> result = lessonService.getTutorLessons(2);

        assertThat(result).containsExactly(lr);
    }

    // ─── changeLessonStatus ───────────────────────────────────────────────────

    @Nested
    @DisplayName("changeLessonStatus – maszyna stanów")
    class ChangeLessonStatus {

        private Lesson buildLesson(LessonStatus status) {
            Lesson lesson = new Lesson();
            lesson.setId(1);
            lesson.setLessonDate(LocalDate.now().plusDays(1));
            lesson.setTimeFrom(LocalTime.of(10, 0));
            lesson.setTimeTo(LocalTime.of(11, 0));
            lesson.setLessonStatus(status);
            return lesson;
        }

        private User tutorUserRole() {
            return User.builder().id(10).userRole(UserRole.TUTOR).isActive(true).build();
        }

        private User studentUserRole() {
            return User.builder().id(20).userRole(UserRole.STUDENT).isActive(true).build();
        }

        private User adminUserRole() {
            return User.builder().id(30).userRole(UserRole.ADMIN).isActive(true).build();
        }

        @Test
        @DisplayName("PENDING → CONFIRMED: może tylko TUTOR")
        void pendingToConfirmed_byTutor_success() {
            Lesson lesson = buildLesson(LessonStatus.PENDING);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);
            LessonResponse lr = mock(LessonResponse.class);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
            when(lessonRepository.save(lesson)).thenReturn(lesson);
            when(lessonMapper.toResponse(lesson)).thenReturn(lr);

            LessonResponse result = lessonService.changeLessonStatus(1, req, tutorUserRole());

            assertThat(lesson.getLessonStatus()).isEqualTo(LessonStatus.CONFIRMED);
            assertThat(result).isEqualTo(lr);
        }

        @Test
        @DisplayName("PENDING → CONFIRMED: student nie może potwierdzić")
        void pendingToConfirmed_byStudent_throws() {
            Lesson lesson = buildLesson(LessonStatus.PENDING);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

            assertThatThrownBy(() -> lessonService.changeLessonStatus(1, req, studentUserRole()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("PENDING → CANCELLED: każdy może anulować")
        void pendingToCancelled_byStudent_success() {
            Lesson lesson = buildLesson(LessonStatus.PENDING);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CANCELLED, null);
            LessonResponse lr = mock(LessonResponse.class);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
            when(lessonRepository.save(lesson)).thenReturn(lesson);
            when(lessonMapper.toResponse(lesson)).thenReturn(lr);

            lessonService.changeLessonStatus(1, req, studentUserRole());

            assertThat(lesson.getLessonStatus()).isEqualTo(LessonStatus.CANCELLED);
        }

        @Test
        @DisplayName("COMPLETED → CONFIRMED: przejście niedozwolone")
        void completedToAny_throws() {
            Lesson lesson = buildLesson(LessonStatus.COMPLETED);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

            assertThatThrownBy(() -> lessonService.changeLessonStatus(1, req, tutorUserRole()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("CANCELLED → CONFIRMED: przejście niedozwolone")
        void cancelledToAny_throws() {
            Lesson lesson = buildLesson(LessonStatus.CANCELLED);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

            assertThatThrownBy(() -> lessonService.changeLessonStatus(1, req, tutorUserRole()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("ten sam status: przejście do tego samego statusu niedozwolone")
        void sameStatus_throws() {
            Lesson lesson = buildLesson(LessonStatus.PENDING);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.PENDING, null);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

            assertThatThrownBy(() -> lessonService.changeLessonStatus(1, req, tutorUserRole()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("ADMIN może zmienić każdy status (np. COMPLETED → CONFIRMED)")
        void admin_canChangeAnyStatus() {
            Lesson lesson = buildLesson(LessonStatus.COMPLETED);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);
            LessonResponse lr = mock(LessonResponse.class);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
            when(lessonRepository.save(lesson)).thenReturn(lesson);
            when(lessonMapper.toResponse(lesson)).thenReturn(lr);

            lessonService.changeLessonStatus(1, req, adminUserRole());

            assertThat(lesson.getLessonStatus()).isEqualTo(LessonStatus.CONFIRMED);
        }

        @Test
        @DisplayName("changeLessonStatus – błąd: lekcja nie istnieje")
        void changeLessonStatus_lessonNotFound() {
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CONFIRMED, null);
            when(lessonRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.changeLessonStatus(99, req, tutorUserRole()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("tutorNotes zostają zaktualizowane przy zmianie statusu")
        void changeLessonStatus_updatesTutorNotes() {
            Lesson lesson = buildLesson(LessonStatus.CONFIRMED);
            LessonStatusRequest req = new LessonStatusRequest(LessonStatus.CANCELLED, "Notatka tutora");
            LessonResponse lr = mock(LessonResponse.class);

            when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
            when(lessonRepository.save(lesson)).thenReturn(lesson);
            when(lessonMapper.toResponse(lesson)).thenReturn(lr);

            lessonService.changeLessonStatus(1, req, tutorUserRole());

            assertThat(lesson.getTutorNotes()).isEqualTo("Notatka tutora");
        }
    }
}
