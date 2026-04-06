package pl.edu.ur.teachly.lesson.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.subject.entity.Subject;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lessons")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "lesson_date", nullable = false)
    private LocalDate lessonDate;

    @Column(name = "time_from", nullable = false)
    private LocalTime time_from;

    @Column(name = "time_to", nullable = false)
    private LocalTime time_to;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_status", nullable = false)
    @Builder.Default
    private LessonStatus lessonStatus = LessonStatus.PENDING;

    @Column(name = "tutor_notes", columnDefinition = "TEXT")
    private String tutorNotes;

    @Column(name = "student_notes", columnDefinition = "TEXT")
    private String studentNotes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
