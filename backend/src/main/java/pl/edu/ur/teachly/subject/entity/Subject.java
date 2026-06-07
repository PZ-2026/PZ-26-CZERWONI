package pl.edu.ur.teachly.subject.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Encja przedmiotu nauczania (np. "Algebra liniowa", "Angielski A2").
 *
 * <p>Każdy przedmiot jest przypisany do jednej {@link SubjectCategory}. Nazwa przedmiotu jest
 * unikalna w skali całego systemu.
 */
@Entity
@Table(name = "subjects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_name", nullable = false, unique = true, length = 100)
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SubjectCategory category;
}
