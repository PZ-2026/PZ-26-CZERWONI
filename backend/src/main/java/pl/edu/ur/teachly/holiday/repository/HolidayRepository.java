package pl.edu.ur.teachly.holiday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.ur.teachly.holiday.entity.Holiday;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {
    Optional<Holiday> findByHolidayDate(LocalDate holidayDate);

    boolean existsByHolidayDate(LocalDate holidayDate);

    java.util.List<Holiday> findByHolidayDateBetween(LocalDate startDate, LocalDate endDate);
}
