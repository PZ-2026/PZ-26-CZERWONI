package pl.edu.ur.teachly.tutor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDayResponse {
    private LocalDate date;
    private List<TimeSlot> availableSlots;
}

