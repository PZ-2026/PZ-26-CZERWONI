package pl.edu.ur.teachly.tutor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.Tutor;

/**
 * Repozytorium JPA dla encji {@link Tutor}.
 *
 * <p>Zawiera zapytanie do wyszukiwania aktywnych korepetytorów z opcjonalnym filtrowaniem po frazie
 * (imię, nazwisko, e-mail) oraz po nazwie przedmiotu.
 */
@Repository
public interface TutorRepository extends JpaRepository<Tutor, Integer> {

    @Query(
            """
                    SELECT DISTINCT t FROM Tutor t
                    JOIN FETCH t.user u
                    WHERE u.isActive = true
                      AND (:pattern IS NULL OR
                           LOWER(u.firstName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(u.lastName) LIKE :pattern ESCAPE '\\' OR
                           LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE :pattern ESCAPE '\\' OR
                           LOWER(u.email) LIKE :pattern ESCAPE '\\')
                      AND (:subjectName IS NULL OR EXISTS (
                           SELECT 1 FROM TutorSubject ts JOIN ts.subject s
                           WHERE ts.tutor = t AND LOWER(s.subjectName) = :subjectName))
                    ORDER BY u.lastName, u.firstName
                    """)
    List<Tutor> searchActiveTutors(
            @Param("pattern") String pattern, @Param("subjectName") String subjectName);
}
