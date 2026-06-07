package pl.edu.ur.teachly.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.admin.dto.response.AdminStatsResponse;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.subject.repository.SubjectCategoryRepository;
import pl.edu.ur.teachly.subject.repository.SubjectRepository;
import pl.edu.ur.teachly.user.repository.UserRepository;

/**
 * Serwis dostarczający zagregowane statystyki systemu dla panelu administratora.
 *
 * <p>Zbiera dane z repozytoriów użytkowników, lekcji, przedmiotów, dni wolnych i opinii, a
 * następnie zwraca je w postaci jednego obiektu odpowiedzi.
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectCategoryRepository categoryRepository;
    private final HolidayRepository holidayRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Zwraca zagregowane statystyki systemu.
     *
     * <p>Zlicza użytkowników z podziałem na role (student, korepetytor, admin), lekcje z podziałem
     * na statusy (oczekująca, potwierdzona, zakończona, anulowana) oraz łączną liczbę przedmiotów,
     * kategorii, dni wolnych i opinii.
     *
     * @return obiekt ze wszystkimi statystykami systemu
     */
    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        int totalStudents = 0;
        int totalTutors = 0;
        int totalAdmins = 0;
        for (Object[] row : userRepository.countGroupedByRole()) {
            UserRole role = (UserRole) row[0];
            int count = ((Long) row[1]).intValue();
            switch (role) {
                case STUDENT -> totalStudents = count;
                case TUTOR -> totalTutors = count;
                case ADMIN -> totalAdmins = count;
                default -> {}
            }
        }

        int pendingLessons = 0;
        int confirmedLessons = 0;
        int completedLessons = 0;
        int cancelledLessons = 0;
        for (Object[] row : lessonRepository.countGroupedByStatus()) {
            LessonStatus status = (LessonStatus) row[0];
            int count = ((Long) row[1]).intValue();
            switch (status) {
                case PENDING -> pendingLessons = count;
                case CONFIRMED -> confirmedLessons = count;
                case COMPLETED -> completedLessons = count;
                case CANCELLED -> cancelledLessons = count;
                default -> {}
            }
        }

        return new AdminStatsResponse(
                totalStudents + totalTutors + totalAdmins,
                totalStudents,
                totalTutors,
                totalAdmins,
                pendingLessons + confirmedLessons + completedLessons + cancelledLessons,
                pendingLessons,
                confirmedLessons,
                completedLessons,
                cancelledLessons,
                (int) subjectRepository.count(),
                (int) categoryRepository.count(),
                (int) holidayRepository.count(),
                (int) reviewRepository.count());
    }
}
