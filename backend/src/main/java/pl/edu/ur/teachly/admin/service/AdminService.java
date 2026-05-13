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

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectCategoryRepository categoryRepository;
    private final HolidayRepository holidayRepository;
    private final ReviewRepository reviewRepository;

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
