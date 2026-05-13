package pl.edu.ur.teachly.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Review JOIN Tutor
    @Query("SELECT r FROM Review r JOIN r.tutor t WHERE t.userId = :tutorId")
    List<Review> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    // Review JOIN User (student)
    @Query("SELECT r FROM Review r JOIN r.student s WHERE s.id = :studentId")
    List<Review> findByStudent_Id(@Param("studentId") Integer studentId);

    // Review JOIN Tutor
    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.tutor t WHERE t.userId = :tutorId")
    java.math.BigDecimal findAverageRatingByTutorId(@Param("tutorId") Integer tutorId);

    @Query(
            "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r JOIN r.student s JOIN r.tutor t WHERE s.id = :studentId AND t.userId = :tutorUserId")
    boolean existsByStudentAndTutor(
            @Param("studentId") Integer studentId, @Param("tutorUserId") Integer tutorUserId);
}
