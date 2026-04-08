package pl.edu.ur.teachly.tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;

import java.util.List;

@Repository
public interface TutorAvailabilityOverrideRepository extends JpaRepository<TutorAvailabilityOverride, Integer> {
    List<TutorAvailabilityOverride> findByTutor_UserId(Integer tutorId);

    List<TutorAvailabilityOverride> findByTutor_UserIdAndOverrideDateBetween(Integer tutorId, java.time.LocalDate startDate, java.time.LocalDate endDate);
}

