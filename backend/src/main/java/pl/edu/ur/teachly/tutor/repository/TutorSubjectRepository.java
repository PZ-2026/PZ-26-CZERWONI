package pl.edu.ur.teachly.tutor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;

@Repository
public interface TutorSubjectRepository extends JpaRepository<TutorSubject, Integer> {

    // TutorSubject JOIN Tutor
    @Query("SELECT ts FROM TutorSubject ts JOIN ts.tutor t WHERE t.userId = :tutorId")
    List<TutorSubject> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    @Query(
            """
                    SELECT ts FROM TutorSubject ts
                    JOIN FETCH ts.subject
                    JOIN ts.tutor t
                    WHERE t.userId IN :tutorIds
                    """)
    List<TutorSubject> findByTutor_UserIdIn(@Param("tutorIds") List<Integer> tutorIds);

    // TutorSubject JOIN Subject JOIN SubjectCategory
    @Query(
            """
            SELECT ts FROM TutorSubject ts
            JOIN ts.subject s
            JOIN s.category c
            WHERE c.id = :categoryId
            """)
    List<TutorSubject> findBySubjectCategoryId(@Param("categoryId") Integer categoryId);
}
