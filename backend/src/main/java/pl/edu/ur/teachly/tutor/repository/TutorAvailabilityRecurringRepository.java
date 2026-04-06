package pl.edu.ur.teachly.tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;

import java.util.List;

@Repository
public interface TutorAvailabilityRecurringRepository extends JpaRepository<TutorAvailabilityRecurring, Integer> {
    List<TutorAvailabilityRecurring> findByTutor_UserId(Integer tutorId);
}

