package pl.edu.ur.teachly.review.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Encja opinii wystawionej korepetytorowi przez ucznia.
 *
 * <p>Każdy uczeń może wystawić danemu korepetytorowi tylko jedną opinię, i tylko po odbyciu co
 * najmniej jednej zakończonej lekcji. Ocena przechowywana jest jako liczba dziesiętna z jednym
 * miejscem po przecinku (np. 4.5).
 */
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    /** Ocena w skali 1.0–5.0 z krokiem 0.5. */
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
