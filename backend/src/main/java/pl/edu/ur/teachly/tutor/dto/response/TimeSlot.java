package pl.edu.ur.teachly.tutor.dto.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reprezentuje wolny slot czasowy w planie korepetytora (godzina od–do).
 *
 * <p>Używany przez {@link TimetableDayResponse} jako element listy dostępnych terminów.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    private LocalTime timeFrom;
    private LocalTime timeTo;
}
