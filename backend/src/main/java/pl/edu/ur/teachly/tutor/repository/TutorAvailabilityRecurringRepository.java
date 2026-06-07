package pl.edu.ur.teachly.tutor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;

/**
 * Repozytorium JPA dla encji {@link TutorAvailabilityRecurring}.
 *
 * <p>Umożliwia pobieranie cyklicznych slotów dostępności przypisanych do konkretnego korepetytora.
 */
@Repository
public interface TutorAvailabilityRecurringRepository
        extends JpaRepository<TutorAvailabilityRecurring, Integer> {

    // TutorAvailabilityRecurring JOIN Tutor
    @Query(
            "SELECT tar FROM TutorAvailabilityRecurring tar JOIN tar.tutor t WHERE t.userId = :tutorId")
    List<TutorAvailabilityRecurring> findByTutor_UserId(@Param("tutorId") Integer tutorId);
}
