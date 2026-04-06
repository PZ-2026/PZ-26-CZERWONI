package pl.edu.ur.teachly.tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorSubject;

import java.util.List;

@Repository
public interface TutorSubjectRepository extends JpaRepository<TutorSubject, Integer> {
    List<TutorSubject> findByTutor_UserId(Integer tutorId);
}
