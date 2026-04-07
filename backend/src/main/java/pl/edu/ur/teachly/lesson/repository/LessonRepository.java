package pl.edu.ur.teachly.lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.lesson.entity.Lesson;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByTutor_UserId(Integer tutorId);

    List<Lesson> findByStudent_Id(Integer studentId);

    List<Lesson> findByTutor_UserIdAndLessonDate(Integer tutorId, LocalDate lessonDate);

    boolean existsByStudent_IdAndTutor_UserIdAndLessonStatus(Integer studentId, Integer tutorId, pl.edu.ur.teachly.common.enums.LessonStatus status);
}
