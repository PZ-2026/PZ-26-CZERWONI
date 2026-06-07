package pl.edu.ur.teachly.subject.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Encja kategorii przedmiotów (np. "Matematyka", "Języki obce").
 *
 * <p>Kategorii nie można usunąć, gdy posiada przypisane przedmioty.
 */
@Entity
@Table(name = "subject_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;
}
