package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

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

    @Column(name = "time_from")
    private LocalTime timeFrom;

    @Column(name = "time_to")
    private LocalTime timeTo;
}
