package pl.edu.ur.teachly.tutor.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.edu.ur.teachly.subject.entity.Subject;

@Entity
@Table(name = "tutor_subjects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "level_primary", nullable = false)
    @Builder.Default
    private Boolean levelPrimary = false;

    @Column(name = "level_high_school", nullable = false)
    @Builder.Default
    private Boolean levelHighSchool = false;

    @Column(name = "level_university", nullable = false)
    @Builder.Default
    private Boolean levelUniversity = false;

    @Column(name = "level_exam_prep", nullable = false)
    @Builder.Default
    private Boolean levelExamPrep = false;

    @Column(name = "level_professional", nullable = false)
    @Builder.Default
    private Boolean levelProfessional = false;
}
