package pl.edu.ur.teachly.lesson.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.lesson.entity.Lesson;

/**
 * Repozytorium JPA dla encji {@link Lesson}.
 *
 * <p>Zawiera zapytania do pobierania lekcji ucznia i korepetytora, wykrywania konfliktów terminów
 * oraz wyszukiwania z wielokryterialnym filtrowaniem dla panelu administratora.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    @Query("SELECT l FROM Lesson l JOIN l.tutor t WHERE t.userId = :tutorId")
    List<Lesson> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    @Query("SELECT l FROM Lesson l JOIN l.student s WHERE s.id = :studentId")
    List<Lesson> findByStudent_Id(@Param("studentId") Integer studentId);

    @Query(
            """
                    SELECT l FROM Lesson l
                    JOIN l.tutor t
                    WHERE t.userId = :tutorId
                      AND l.lessonDate = :lessonDate
                    """)
    List<Lesson> findByTutor_UserIdAndLessonDate(
            @Param("tutorId") Integer tutorId, @Param("lessonDate") LocalDate lessonDate);

    @Query(
            """
                    SELECT COUNT(l) > 0 FROM Lesson l
                    JOIN l.student s
                    JOIN l.tutor t
                    WHERE s.id = :studentId
                      AND t.userId = :tutorId
                      AND l.lessonStatus = :status
                    """)
    boolean existsByStudent_IdAndTutor_UserIdAndLessonStatus(
            @Param("studentId") Integer studentId,
            @Param("tutorId") Integer tutorId,
            @Param("status") LessonStatus status);

    @Query(
            """
                    SELECT l FROM Lesson l
                    JOIN FETCH l.student
                    JOIN FETCH l.subject
                    JOIN FETCH l.tutor t
                    WHERE t.userId = :tutorId
                      AND l.lessonDate BETWEEN :startDate AND :endDate
                    """)
    List<Lesson> findByTutor_UserIdAndLessonDateBetween(
            @Param("tutorId") Integer tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(
            """
                    SELECT l FROM Lesson l
                    JOIN FETCH l.tutor t
                    JOIN FETCH l.subject
                    JOIN FETCH l.student s
                    WHERE s.id = :studentId
                      AND l.lessonDate BETWEEN :startDate AND :endDate
                    """)
    List<Lesson> findByStudent_IdAndLessonDateBetween(
            @Param("studentId") Integer studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(
            """
                    SELECT l FROM Lesson l
                    JOIN FETCH l.tutor t
                    JOIN FETCH l.student
                    JOIN FETCH l.subject
                    WHERE l.lessonDate BETWEEN :startDate AND :endDate
                    """)
    List<Lesson> findByLessonDateBetween(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(
            """
                    SELECT COUNT(l) > 0
                    FROM Lesson l
                    JOIN l.tutor t
                    WHERE t.userId = :tutorId
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
            @Param("status") LessonStatus status);

    @Query(
            """
                    SELECT COUNT(l) > 0
                    FROM Lesson l
                    JOIN l.student s
                    WHERE s.id = :studentId
                      AND l.lessonDate = :date
                      AND l.lessonStatus <> :cancelledStatus
                      AND l.timeFrom < :timeTo
                      AND l.timeTo > :timeFrom
                    """)
    boolean existsConflictingStudentLesson(
            @Param("studentId") Integer studentId,
            @Param("date") LocalDate date,
            @Param("timeFrom") LocalTime timeFrom,
            @Param("timeTo") LocalTime timeTo,
            @Param("cancelledStatus") LessonStatus cancelledStatus);

    @Query("SELECT l.lessonStatus, COUNT(l) FROM Lesson l GROUP BY l.lessonStatus")
    List<Object[]> countGroupedByStatus();

    @Query(
            """
                    SELECT DISTINCT l FROM Lesson l
                    JOIN FETCH l.tutor t
                    JOIN FETCH t.user tu
                    JOIN FETCH l.student s
                    JOIN FETCH l.subject sub
                    WHERE (:pattern IS NULL OR
                           LOWER(tu.firstName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(tu.lastName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(s.firstName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(s.lastName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(sub.subjectName) LIKE :pattern ESCAPE '\\')
                      AND (:status IS NULL OR l.lessonStatus = :status)
                      AND (:paymentStatus IS NULL OR l.paymentStatus = :paymentStatus)
                      AND (:format IS NULL OR l.format = :format)
                      AND (:upcoming IS NULL OR
                           (:upcoming = TRUE AND l.lessonDate >= :today) OR
                           (:upcoming = FALSE AND l.lessonDate < :today))
                    ORDER BY l.lessonDate DESC, l.timeFrom
                    """)
    List<Lesson> searchLessons(
            @Param("pattern") String pattern,
            @Param("status") LessonStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("format") LessonFormat format,
            @Param("upcoming") Boolean upcoming,
            @Param("today") LocalDate today);
}
