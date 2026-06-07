package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

/**
 * Encja jednorazowego nadpisania dostępności korepetytora na konkretną datę.
 *
 * <p>Nadpisanie ma wyższy priorytet niż wpisy cykliczne. Jeśli {@code timeFrom} i {@code timeTo} są
 * {@code null}, cały dzień jest zablokowany (korepetytor niedostępny). W przeciwnym razie
 * dostępność w tym dniu jest ograniczona do podanego przedziału.
 */
@Entity
@Table(name = "tutor_availability_override")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailabilityOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(name = "override_date", nullable = false)
    private LocalDate overrideDate;

    /** Godzina początku dostępności; {@code null} oznacza zablokowanie całego dnia. */
    @Column(name = "time_from")
    private LocalTime timeFrom;

    /** Godzina końca dostępności; {@code null} oznacza zablokowanie całego dnia. */
    @Column(name = "time_to")
    private LocalTime timeTo;
}
