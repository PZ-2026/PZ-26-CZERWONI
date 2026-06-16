package pl.edu.ur.teachly.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.review.entity.Review;

/**
 * Repozytorium JPA dla encji {@link Review}.
 *
 * <p>Zawiera zapytania do pobierania opinii korepetytora i ucznia, obliczania średniej oceny oraz
 * wyszukiwania z filtrowaniem dla panelu administratora.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r JOIN r.tutor t WHERE t.userId = :tutorId")
    List<Review> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    @Query("SELECT r FROM Review r JOIN r.student s WHERE s.id = :studentId")
    List<Review> findByStudent_Id(@Param("studentId") Integer studentId);

    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.tutor t WHERE t.userId = :tutorId")
    java.math.BigDecimal findAverageRatingByTutorId(@Param("tutorId") Integer tutorId);

    @Query(
            "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
                    + "FROM Review r JOIN r.student s JOIN r.tutor t "
                    + "WHERE s.id = :studentId AND t.userId = :tutorUserId")
    boolean existsByStudentAndTutor(
            @Param("studentId") Integer studentId, @Param("tutorUserId") Integer tutorUserId);

    @Query(
            """
                    SELECT t.userId, AVG(r.rating), COUNT(r)
                    FROM Review r JOIN r.tutor t
                    WHERE t.userId IN :tutorIds
                    GROUP BY t.userId
                    """)
    List<Object[]> findRatingStatsByTutorIds(@Param("tutorIds") List<Integer> tutorIds);

    @Query(
            """
                    SELECT r FROM Review r
                    JOIN FETCH r.tutor t
                    JOIN FETCH t.user tu
                    JOIN FETCH r.student s
                    WHERE (:pattern IS NULL OR
                           LOWER(tu.firstName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(tu.lastName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(s.firstName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(s.lastName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(r.comment) LIKE :pattern ESCAPE '\\')
                      AND (:rating IS NULL OR r.rating = :rating)
                    ORDER BY r.createdAt DESC
                    """)
    List<Review> searchReviews(
            @Param("pattern") String pattern, @Param("rating") java.math.BigDecimal rating);
}
