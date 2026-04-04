package pl.edu.ur.teachly.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutor_subjects")
@Getter
@Setter
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
    private Boolean levelPrimary = false;

    @Column(name = "level_high_school", nullable = false)
    private Boolean levelHighSchool = false;

    @Column(name = "level_university", nullable = false)
    private Boolean levelUniversity = false;

    @Column(name = "level_exam_prep", nullable = false)
    private Boolean levelExamPrep = false;

    @Column(name = "level_professional", nullable = false)
    private Boolean levelProfessional = false;
}
