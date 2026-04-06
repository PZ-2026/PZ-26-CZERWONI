package pl.edu.ur.teachly.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.subject.entity.SubjectCategory;

@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Integer> {
}

