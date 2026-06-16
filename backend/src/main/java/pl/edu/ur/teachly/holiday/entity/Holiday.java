package pl.edu.ur.teachly.holiday.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

/**
 * Encja reprezentująca dzień wolny od zajęć (święto) w kalendarzu systemu.
 *
 * <p>Data święta jest unikalna — w danym dniu może istnieć tylko jeden wpis. Dni wolne blokują
 * wszystkie terminy w planie zajęć korepetytorów.
 */
@Entity
@Table(name = "holidays")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(length = 100)
    private String description;
}
