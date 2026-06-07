package pl.edu.ur.teachly.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.subject.entity.Subject;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService – testy jednostkowe")
class ReportServiceTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ReportService reportService;

    private static final LocalDate END = LocalDate.now();
    private static final LocalDate START = END.minusYears(1);

    @Test
    @DisplayName("generateReport – korepetytor, raport lekcji")
    void generateReport_tutorLessonsReport() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(tutor, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – korepetytor, raport przychodów")
    void generateReport_tutorRevenueReport() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(tutor, START, END, "REVENUE", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – korepetytor, raport uczniów")
    void generateReport_tutorStudentsReport() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(tutor, START, END, "STUDENTS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport lekcji")
    void generateReport_studentLessonsReport() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(student, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport wydatków")
    void generateReport_studentExpensesReport() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(student, START, END, "EXPENSES", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport analityczny")
    void generateReport_studentAnalyticsReport() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(student, START, END, "ANALYTICS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport lekcji")
    void generateReport_adminLessonsReport() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(admin, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport przychodów platformy")
    void generateReport_adminRevenueReport() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(admin, START, END, "REVENUE", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport użytkowników")
    void generateReport_adminUsersReport() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>());
        when(userRepository.countByUserRole(UserRole.STUDENT)).thenReturn(10);
        when(userRepository.countByUserRole(UserRole.TUTOR)).thenReturn(5);
        when(userRepository.countByUserRole(UserRole.ADMIN)).thenReturn(1);

        byte[] report = reportService.generateReport(admin, START, END, "USERS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport lekcji z danymi")
    void generateReport_studentLessons_withLessonData() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();
        List<Lesson> lessons =
                List.of(
                        buildLesson(1, student, LessonStatus.COMPLETED, LocalDate.of(2025, 6, 10)),
                        buildLesson(2, student, LessonStatus.PENDING, LocalDate.of(2025, 6, 15)),
                        buildLesson(3, student, LessonStatus.CANCELLED, LocalDate.of(2025, 6, 20)),
                        buildLesson(4, student, LessonStatus.CONFIRMED, LocalDate.of(2025, 6, 25)));

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report = reportService.generateReport(student, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport wydatków z wykresem")
    void generateReport_studentExpenses_withCharts() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();
        List<Lesson> lessons =
                List.of(
                        buildLesson(1, student, LessonStatus.COMPLETED, LocalDate.of(2025, 6, 10)),
                        buildLesson(2, student, LessonStatus.COMPLETED, LocalDate.of(2025, 6, 15)));

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(
                        student, START, END, "EXPENSES", List.of("charts", "price", "subject"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, raport analityczny z wykresem")
    void generateReport_studentAnalytics_withCharts() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();
        List<Lesson> lessons =
                List.of(buildLesson(1, student, LessonStatus.COMPLETED, LocalDate.of(2025, 6, 10)));

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(
                        student,
                        START,
                        END,
                        "ANALYTICS",
                        List.of("charts", "tutor", "subject", "status"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – korepetytor, raport lekcji z danymi")
    void generateReport_tutorLessons_withLessonData() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();
        User student = User.builder().id(3).firstName("Anna").lastName("Kowal").build();
        List<Lesson> lessons =
                List.of(buildLessonForTutor(1, tutor, student, LessonStatus.COMPLETED));

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report = reportService.generateReport(tutor, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – korepetytor, raport przychodów z wykresem")
    void generateReport_tutorRevenue_withCharts() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();
        User student = User.builder().id(3).firstName("Anna").lastName("Kowal").build();
        List<Lesson> lessons =
                List.of(
                        buildLessonForTutor(1, tutor, student, LessonStatus.COMPLETED),
                        buildLessonForTutor(2, tutor, student, LessonStatus.COMPLETED));

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(
                        tutor,
                        START,
                        END,
                        "REVENUE",
                        List.of("charts", "price", "subject", "status"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – korepetytor, raport uczniów z wykresem")
    void generateReport_tutorStudents_withCharts() {
        User tutor =
                User.builder()
                        .id(2)
                        .firstName("Marek")
                        .lastName("Nowak")
                        .userRole(UserRole.TUTOR)
                        .build();
        User student = User.builder().id(3).firstName("Anna").lastName("Kowal").build();
        List<Lesson> lessons =
                List.of(buildLessonForTutor(1, tutor, student, LessonStatus.COMPLETED));

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(eq(2), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(
                        tutor,
                        START,
                        END,
                        "STUDENTS",
                        List.of("charts", "student", "subject", "status"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport lekcji z danymi")
    void generateReport_adminLessons_withLessonData() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();
        User student = User.builder().id(3).firstName("Anna").lastName("Kowal").build();
        User tutorUser = User.builder().id(2).firstName("Marek").lastName("Nowak").build();
        List<Lesson> lessons =
                List.of(buildLessonForTutor(1, tutorUser, student, LessonStatus.COMPLETED));

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report = reportService.generateReport(admin, START, END, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport przychodów z wykresem")
    void generateReport_adminRevenue_withCharts() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();
        User student = User.builder().id(3).firstName("Anna").lastName("Kowal").build();
        User tutorUser = User.builder().id(2).firstName("Marek").lastName("Nowak").build();
        List<Lesson> lessons =
                List.of(buildLessonForTutor(1, tutorUser, student, LessonStatus.COMPLETED));

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(
                        admin,
                        START,
                        END,
                        "REVENUE",
                        List.of("charts", "price", "subject", "status"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – admin, raport użytkowników z wykresem")
    void generateReport_adminUsers_withCharts() {
        User admin =
                User.builder()
                        .id(1)
                        .firstName("Admin")
                        .lastName("Sys")
                        .userRole(UserRole.ADMIN)
                        .build();

        when(lessonRepository.findByLessonDateBetween(eq(START), eq(END)))
                .thenReturn(new ArrayList<>());
        when(userRepository.countByUserRole(UserRole.STUDENT)).thenReturn(10);
        when(userRepository.countByUserRole(UserRole.TUTOR)).thenReturn(5);
        when(userRepository.countByUserRole(UserRole.ADMIN)).thenReturn(1);

        byte[] report =
                reportService.generateReport(
                        admin,
                        START,
                        END,
                        "USERS",
                        List.of("charts", "student", "tutor", "status"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – ten sam dzień start/end")
    void generateReport_sameDayPeriod() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();
        LocalDate day = LocalDate.of(2025, 6, 15);

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(day), eq(day)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(student, day, day, "LESSONS", null);

        assertThat(report).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateReport – uczeń, tylko wybrane pola bez tabeli")
    void generateReport_studentLessons_minimalFields() {
        User student =
                User.builder()
                        .id(3)
                        .firstName("Anna")
                        .lastName("Kowal")
                        .userRole(UserRole.STUDENT)
                        .build();
        List<Lesson> lessons =
                List.of(buildLesson(1, student, LessonStatus.COMPLETED, LocalDate.of(2025, 6, 10)));

        when(lessonRepository.findByStudent_IdAndLessonDateBetween(eq(3), eq(START), eq(END)))
                .thenReturn(new ArrayList<>(lessons));

        byte[] report =
                reportService.generateReport(student, START, END, "lessons", List.of("notes"));

        assertThat(report).isNotNull().isNotEmpty();
    }

    private Lesson buildLesson(int id, User student, LessonStatus status, LocalDate date) {
        User tutorUser = User.builder().id(2).firstName("Jan").lastName("Kowalski").build();
        Tutor tutor = Tutor.builder().userId(2).user(tutorUser).build();
        Subject subject = Subject.builder().id(1).subjectName("Matematyka").build();
        return Lesson.builder()
                .id(id)
                .student(student)
                .tutor(tutor)
                .subject(subject)
                .lessonDate(date)
                .timeFrom(LocalTime.of(10, 0))
                .timeTo(LocalTime.of(11, 0))
                .lessonStatus(status)
                .amount(BigDecimal.valueOf(80))
                .format(LessonFormat.ONLINE)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    private Lesson buildLessonForTutor(int id, User tutorUser, User student, LessonStatus status) {
        Tutor tutor = Tutor.builder().userId(tutorUser.getId()).user(tutorUser).build();
        Subject subject = Subject.builder().id(1).subjectName("Fizyka").build();
        return Lesson.builder()
                .id(id)
                .student(student)
                .tutor(tutor)
                .subject(subject)
                .lessonDate(LocalDate.of(2025, 6, 10))
                .timeFrom(LocalTime.of(14, 0))
                .timeTo(LocalTime.of(15, 30))
                .lessonStatus(status)
                .amount(BigDecimal.valueOf(100))
                .format(LessonFormat.IN_PERSON)
                .paymentStatus(PaymentStatus.PAID)
                .build();
    }
}
