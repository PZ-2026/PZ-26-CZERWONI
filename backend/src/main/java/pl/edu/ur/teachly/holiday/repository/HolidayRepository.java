package pl.edu.ur.teachly.holiday.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.holiday.entity.Holiday;

/**
 * Repozytorium JPA dla encji {@link Holiday}.
 *
 * <p>Umożliwia sprawdzenie unikalności daty oraz pobieranie dni wolnych z podanego przedziału dat
 * (używane przez {@code TimetableService}).
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    @Query("SELECT h FROM Holiday h WHERE h.holidayDate = :holidayDate")
    Optional<Holiday> findByHolidayDate(@Param("holidayDate") LocalDate holidayDate);

    @Query("SELECT COUNT(h) > 0 FROM Holiday h WHERE h.holidayDate = :holidayDate")
    boolean existsByHolidayDate(@Param("holidayDate") LocalDate holidayDate);

    @Query("SELECT h FROM Holiday h WHERE h.holidayDate BETWEEN :startDate AND :endDate")
    List<Holiday> findByHolidayDateBetween(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
