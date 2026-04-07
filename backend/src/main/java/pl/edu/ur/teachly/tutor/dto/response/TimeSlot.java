package pl.edu.ur.teachly.tutor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    private LocalTime timeFrom;
    private LocalTime timeTo;
}
