package pl.edu.ur.teachly.tutor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;

@Repository
public interface TutorAvailabilityRecurringRepository
        extends JpaRepository<TutorAvailabilityRecurring, Integer> {
    List<TutorAvailabilityRecurring> findByTutor_UserId(Integer tutorId);
}
