package pl.edu.ur.teachly.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.subject.entity.SubjectCategory;

/**
 * Repozytorium JPA dla encji {@link SubjectCategory}.
 *
 * <p>Rozszerza {@link JpaRepository} o standardowe operacje CRUD. Nie zawiera dodatkowych zapytań —
 * operacje na kategoriach są obsługiwane przez bazowe metody.
 */
@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Integer> {}
