package pl.edu.ur.teachly.tutor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;

/**
 * Repozytorium JPA dla encji {@link TutorSubject}.
 *
 * <p>Zawiera zapytania do pobierania przedmiotów korepetytora, sprawdzania duplikatów oraz
 * wczytywania przedmiotów zbiorczo dla wielu korepetytorów (używane przy wyszukiwaniu).
 */
@Repository
public interface TutorSubjectRepository extends JpaRepository<TutorSubject, Integer> {

    // TutorSubject JOIN Tutor
    @Query("SELECT ts FROM TutorSubject ts JOIN ts.tutor t WHERE t.userId = :tutorId")
    List<TutorSubject> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    @Query(
            "SELECT COUNT(ts) > 0 FROM TutorSubject ts JOIN ts.tutor t "
                    + "WHERE t.userId = :tutorId AND ts.subject.id = :subjectId")
    boolean existsByTutor_UserIdAndSubject_Id(
            @Param("tutorId") Integer tutorId, @Param("subjectId") Integer subjectId);

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

    /**
     * Sprawdza, czy dany przedmiot jest przypisany do co najmniej jednego korepetytora.
     *
     * @param subjectId identyfikator przedmiotu
     * @return {@code true} jeśli przedmiot ma co najmniej jedno powiązanie z korepetytorem
     */
    boolean existsBySubjectId(Integer subjectId);
}
