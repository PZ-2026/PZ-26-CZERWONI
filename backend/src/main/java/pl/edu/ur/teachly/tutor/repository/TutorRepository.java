package pl.edu.ur.teachly.tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.Tutor;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Integer> {}
