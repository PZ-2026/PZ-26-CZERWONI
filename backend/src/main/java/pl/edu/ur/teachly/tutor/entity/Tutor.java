package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Encja profilu korepetytora, rozszerzająca konto użytkownika ({@link User}).
 *
 * <p>Relacja 1:1 z {@link User} — klucz główny {@code userId} jest jednocześnie kluczem obcym do
 * tabeli {@code users} (strategia {@code @MapsId}).
 */
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

    /** Opis korepetytora widoczny na jego profilu publicznym. */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /** Stawka godzinowa korepetytora w PLN. */
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "offers_online", nullable = false)
    @Builder.Default
    private Boolean offersOnline = false;

    @Column(name = "offers_in_person", nullable = false)
    @Builder.Default
    private Boolean offersInPerson = false;

    /**
     * Miasto, w którym korepetytor prowadzi zajęcia stacjonarne (null dla zajęć wyłącznie online).
     */
    @Column(name = "city", length = 100)
    private String city;
}
