package pl.edu.ur.teachly.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService – testy jednostkowe")
class ReportServiceTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ReportService reportService;

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
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);

        when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(
                        eq(2), eq(startDate), eq(endDate)))
                .thenReturn(new ArrayList<>());

        byte[] report = reportService.generateReport(tutor, startDate, endDate, "LESSONS", null);

        assertThat(report).isNotNull();
        assertThat(report.length).isGreaterThan(0);
    }
}
