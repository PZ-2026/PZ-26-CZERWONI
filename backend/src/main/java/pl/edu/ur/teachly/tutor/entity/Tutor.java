package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import pl.edu.ur.teachly.user.entity.User;

@Entity
@Table(name = "tutors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {
    @Id private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "offers_online", nullable = false)
    @Builder.Default
    private Boolean offersOnline = false;

    @Column(name = "offers_in_person", nullable = false)
    @Builder.Default
    private Boolean offersInPerson = false;
}
