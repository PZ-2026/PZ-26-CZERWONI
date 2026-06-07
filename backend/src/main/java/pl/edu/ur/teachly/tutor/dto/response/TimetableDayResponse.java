package pl.edu.ur.teachly.tutor.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Odpowiedź reprezentująca dostępne terminy korepetytora w konkretnym dniu.
 *
 * <p>Jeśli {@code availableSlots} jest pustą listą, korepetytor jest w tym dniu niedostępny
 * (święto, brak dostępności cyklicznej lub nadpisanie blokujące cały dzień).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDayResponse {
    private LocalDate date;
    private List<TimeSlot> availableSlots;
}
