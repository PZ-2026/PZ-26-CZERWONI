package pl.edu.ur.teachly.subject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.subject.entity.Subject;

/**
 * Repozytorium JPA dla encji {@link Subject}.
 *
 * <p>Dostarcza metodę do pobierania przedmiotów przypisanych do danej kategorii — używaną przy
 * walidacji przed usunięciem kategorii.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    // Subject JOIN SubjectCategory
    @Query("SELECT s FROM Subject s JOIN s.category c WHERE c.id = :categoryId")
    List<Subject> findByCategory_Id(@Param("categoryId") Integer categoryId);
}
