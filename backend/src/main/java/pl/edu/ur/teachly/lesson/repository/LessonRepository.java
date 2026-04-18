package pl.edu.ur.teachly.lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.lesson.entity.Lesson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByTutor_UserId(Integer tutorId);

    List<Lesson> findByStudent_Id(Integer studentId);

    List<Lesson> findByTutor_UserIdAndLessonDate(Integer tutorId, LocalDate lessonDate);

    boolean existsByStudent_IdAndTutor_UserIdAndLessonStatus(Integer studentId, Integer tutorId, LessonStatus status);

    List<Lesson> findByTutor_UserIdAndLessonDateBetween(Integer tutorId, LocalDate startDate, LocalDate endDate);

    @Query("""
                SELECT COUNT(l) > 0
                FROM Lesson l
                WHERE l.tutor.userId = :tutorId
                  AND l.lessonDate = :date
                  AND l.lessonStatus = :status
                  AND l.timeFrom < :timeTo
                  AND l.timeTo > :timeFrom
            """)
    boolean existsConflictingLesson(
            @Param("tutorId") Integer tutorId,
            @Param("date") LocalDate date,
            @Param("timeFrom") LocalTime timeFrom,
            @Param("timeTo") LocalTime timeTo,
            @Param("status") LessonStatus status
    );
}