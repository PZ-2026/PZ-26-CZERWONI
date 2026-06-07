package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

/**
 * Encja cyklicznej dostępności korepetytora — powtarza się co tydzień.
 *
 * <p>Definiuje przedział godzinowy w konkretnym dniu tygodnia ({@code dayOfWeek} zgodnie z ISO: 1 =
 * poniedziałek, 7 = niedziela). Opcjonalne pole {@code dateTo} ogranicza ważność wpisu do podanej
 * daty — po jej przekroczeniu slot jest ignorowany.
 */
@Entity
@Table(name = "tutor_availability_recurring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailabilityRecurring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    /** Dzień tygodnia (ISO): 1 = poniedziałek, 7 = niedziela. */
    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "time_from", nullable = false)
    private LocalTime timeFrom;

    @Column(name = "time_to", nullable = false)
    private LocalTime timeTo;

    /** Data końcowa ważności wpisu cyklicznego; {@code null} oznacza brak ograniczenia. */
    @Column(name = "date_to")
    private LocalDate dateTo;
}
