package pl.edu.ur.teachly.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.admin.dto.response.AdminStatsResponse;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.subject.repository.SubjectCategoryRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService – testy jednostkowe")
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private SubjectCategoryRepository categoryRepository;
    @Mock private HolidayRepository holidayRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private AdminService adminService;

    @Test
    @DisplayName("getStats – zwraca poprawne statystyki")
    void getStats_returnsCorrectStats() {
        when(userRepository.countGroupedByRole())
                .thenReturn(
                        List.of(
                                new Object[] {UserRole.STUDENT, 10L},
                                new Object[] {UserRole.TUTOR, 5L},
                                new Object[] {UserRole.ADMIN, 1L}));

        when(lessonRepository.countGroupedByStatus())
                .thenReturn(
                        List.of(
                                new Object[] {LessonStatus.PENDING, 2L},
                                new Object[] {LessonStatus.CONFIRMED, 3L},
                                new Object[] {LessonStatus.COMPLETED, 4L},
                                new Object[] {LessonStatus.CANCELLED, 1L}));

        when(subjectRepository.count()).thenReturn(20L);
        when(categoryRepository.count()).thenReturn(5L);
        when(holidayRepository.count()).thenReturn(10L);
        when(reviewRepository.count()).thenReturn(15L);

        AdminStatsResponse stats = adminService.getStats();

        assertThat(stats.totalUsers()).isEqualTo(16);
        assertThat(stats.totalStudents()).isEqualTo(10);
        assertThat(stats.totalTutors()).isEqualTo(5);
        assertThat(stats.totalAdmins()).isEqualTo(1);
        assertThat(stats.totalLessons()).isEqualTo(10);
        assertThat(stats.pendingLessons()).isEqualTo(2);
        assertThat(stats.confirmedLessons()).isEqualTo(3);
        assertThat(stats.completedLessons()).isEqualTo(4);
        assertThat(stats.cancelledLessons()).isEqualTo(1);
        assertThat(stats.totalSubjects()).isEqualTo(20);
        assertThat(stats.totalCategories()).isEqualTo(5);
        assertThat(stats.totalHolidays()).isEqualTo(10);
        assertThat(stats.totalReviews()).isEqualTo(15);
    }
}
