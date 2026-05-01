package pl.edu.ur.teachly.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.entity.Review;
import pl.edu.ur.teachly.review.mapper.ReviewMapper;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService - testy jednostkowe")
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private UserRepository userRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private LessonRepository lessonRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User student;
    private Tutor tutor;
    private ReviewRequest reviewRequest;
    private Review review;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        student = User.builder().id(1).firstName("Student").lastName("Kowalski").build();
        User tutorUser = User.builder().id(2).firstName("Tutor").lastName("Nowak").build();
        tutor = Tutor.builder().userId(2).user(tutorUser).build();
        
        reviewRequest = new ReviewRequest(2, BigDecimal.valueOf(4.5), "Super nauczyciel");
        
        review = Review.builder()
                .id(1)
                .student(student)
                .tutor(tutor)
                .rating(BigDecimal.valueOf(4.5))
                .comment("Super nauczyciel")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        reviewResponse = new ReviewResponse(
                1, 2, "Tutor", "Nowak", 1, "Student", "Kowalski",
                BigDecimal.valueOf(4.5), "Super nauczyciel",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("addReview - sukces: zapisuje opinię")
    void addReview_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor));
        when(reviewMapper.toEntity(reviewRequest)).thenReturn(new Review());
        when(lessonRepository.existsByStudent_IdAndTutor_UserIdAndLessonStatus(1, 2, LessonStatus.COMPLETED))
                .thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

        ReviewResponse result = reviewService.addReview(1, reviewRequest);

        assertThat(result).isEqualTo(reviewResponse);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("addReview - błąd: brak ukończonej lekcji")
    void addReview_noCompletedLesson_throwsBusinessValidationException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(2)).thenReturn(Optional.of(tutor));
        when(reviewMapper.toEntity(reviewRequest)).thenReturn(new Review());
        when(lessonRepository.existsByStudent_IdAndTutor_UserIdAndLessonStatus(1, 2, LessonStatus.COMPLETED))
                .thenReturn(false);

        assertThatThrownBy(() -> reviewService.addReview(1, reviewRequest))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("nie odbyłeś jeszcze żadnej lekcji");
    }

    @Test
    @DisplayName("addReview - błąd: student nie istnieje")
    void addReview_studentNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(1, reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ucznia");
    }

    @Test
    @DisplayName("addReview - błąd: tutor nie istnieje")
    void addReview_tutorNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(1, reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("korepetytora");
    }

    @Test
    @DisplayName("updateReview - sukces: aktualizuje opinię")
    void updateReview_success() {
        ReviewRequest updateRequest = new ReviewRequest(2, BigDecimal.valueOf(5.0), "Zmieniony komentarz");
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse); // ignorujemy zmienione wartosci dla uproszczenia

        reviewService.updateReview(1, updateRequest);

        assertThat(review.getRating()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(review.getComment()).isEqualTo("Zmieniony komentarz");
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("updateReview - błąd: opinia nie istnieje")
    void updateReview_notFound_throwsResourceNotFoundException() {
        when(reviewRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(99, reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("opinii");
    }

    @Test
    @DisplayName("deleteReview - sukces: usuwa opinię")
    void deleteReview_success() {
        when(reviewRepository.existsById(1)).thenReturn(true);

        reviewService.deleteReview(1);

        verify(reviewRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteReview - błąd: opinia nie istnieje")
    void deleteReview_notFound_throwsResourceNotFoundException() {
        when(reviewRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.deleteReview(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("opinii");
    }

    @Test
    @DisplayName("getTutorReviews - zwraca listę opinii")
    void getTutorReviews_returnsList() {
        when(reviewRepository.findByTutor_UserId(2)).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

        List<ReviewResponse> result = reviewService.getTutorReviews(2);

        assertThat(result).containsExactly(reviewResponse);
    }

    @Test
    @DisplayName("getStudentReviews - zwraca listę opinii studenta")
    void getStudentReviews_returnsList() {
        when(reviewRepository.findByStudent_Id(1)).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

        List<ReviewResponse> result = reviewService.getStudentReviews(1);

        assertThat(result).containsExactly(reviewResponse);
    }

    @Test
    @DisplayName("getAllReviews - zwraca wszystkie opinie")
    void getAllReviews_returnsList() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

        List<ReviewResponse> result = reviewService.getAllReviews();

        assertThat(result).containsExactly(reviewResponse);
    }
}
