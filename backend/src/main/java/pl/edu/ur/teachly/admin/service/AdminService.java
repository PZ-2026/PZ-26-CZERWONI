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
        int totalStudents = userRepository.countByUserRole(UserRole.STUDENT);
        int totalTutors = userRepository.countByUserRole(UserRole.TUTOR);
        int totalAdmins = userRepository.countByUserRole(UserRole.ADMIN);
        int totalUsers = totalStudents + totalTutors + totalAdmins;

        int pendingLessons = lessonRepository.countByLessonStatus(LessonStatus.PENDING);
        int confirmedLessons = lessonRepository.countByLessonStatus(LessonStatus.CONFIRMED);
        int completedLessons = lessonRepository.countByLessonStatus(LessonStatus.COMPLETED);
        int cancelledLessons = lessonRepository.countByLessonStatus(LessonStatus.CANCELLED);
        int totalLessons = pendingLessons + confirmedLessons + completedLessons + cancelledLessons;

        return new AdminStatsResponse(
                totalUsers,
                totalStudents,
                totalTutors,
                totalAdmins,
                totalLessons,
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
