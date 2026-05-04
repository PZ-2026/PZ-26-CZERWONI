package pl.edu.ur.teachly.tutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TutorAvailabilityOverrideRepository
        extends JpaRepository<TutorAvailabilityOverride, Integer> {

    // TutorAvailabilityOverride JOIN Tutor
    @Query(
            "SELECT tao FROM TutorAvailabilityOverride tao JOIN tao.tutor t WHERE t.userId = :tutorId")
    List<TutorAvailabilityOverride> findByTutor_UserId(@Param("tutorId") Integer tutorId);

    // TutorAvailabilityOverride JOIN Tutor
    @Query(
            """
                    SELECT tao FROM TutorAvailabilityOverride tao
                    JOIN tao.tutor t
                    WHERE t.userId = :tutorId
                      AND tao.overrideDate BETWEEN :startDate AND :endDate
                    """)
    List<TutorAvailabilityOverride> findByTutor_UserIdAndOverrideDateBetween(
            @Param("tutorId") Integer tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
