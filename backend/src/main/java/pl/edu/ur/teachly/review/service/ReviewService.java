package pl.edu.ur.teachly.review.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.util.SearchQueryUtils;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.entity.Review;
import pl.edu.ur.teachly.review.mapper.ReviewMapper;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.user.repository.UserRepository;

/**
 * Serwis zarządzający opiniami o korepetytorach.
 *
 * <p>Uczeń może wystawić opinię korepetytorowi tylko wtedy, gdy odbyła się co najmniej jedna
 * zakończona lekcja między nimi. Każdy uczeń może wystawić danemu korepetytorowi tylko jedną
 * opinię.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final LessonRepository lessonRepository;

    /**
     * Dodaje nową opinię korepetytorowi wystawioną przez ucznia.
     *
     * @param studentId identyfikator ucznia wystawiającego opinię
     * @param request treść i ocena opinii
     * @return zapisana opinia
     * @throws ResourceNotFoundException gdy uczeń lub korepetytor nie istnieje
     * @throws BusinessValidationException gdy opinia już istnieje lub brak zakończonej lekcji
     */
    @Transactional
    public ReviewResponse addReview(Integer studentId, ReviewRequest request) {
        var student =
                userRepository
                        .findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono ucznia"));
        var tutor =
                tutorRepository
                        .findById(request.tutorId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono korepetytora"));

        if (reviewRepository.existsByStudentAndTutor(studentId, request.tutorId())) {
            throw new BusinessValidationException("Już wystawiłeś opinię temu korepetytorowi");
        }

        boolean hasCompletedLesson =
                lessonRepository.existsByStudent_IdAndTutor_UserIdAndLessonStatus(
                        studentId, request.tutorId(), LessonStatus.COMPLETED);

        if (!hasCompletedLesson) {
            throw new BusinessValidationException(
                    "Nie możesz dodać opinii, ponieważ nie odbyłeś jeszcze żadnej lekcji z tym korepetytorem");
        }

        Review review = reviewMapper.toEntity(request);
        review.setStudent(student);
        review.setTutor(tutor);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    /**
     * Aktualizuje treść i ocenę istniejącej opinii. Tylko jej autor może ją edytować.
     *
     * @param reviewId identyfikator opinii
     * @param request nowa treść i ocena
     * @param callerId identyfikator wywołującego użytkownika
     * @return zaktualizowana opinia
     * @throws ResourceNotFoundException gdy opinia nie istnieje
     * @throws AccessDeniedException gdy wywołujący nie jest autorem opinii
     */
    @Transactional
    public ReviewResponse updateReview(Integer reviewId, ReviewRequest request, Integer callerId) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej opinii"));
        if (!review.getStudent().getId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do edycji tej opinii");
        }
        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    /**
     * Usuwa opinię. Tylko jej autor może ją usunąć.
     *
     * @param reviewId identyfikator opinii
     * @param callerId identyfikator wywołującego użytkownika
     * @throws ResourceNotFoundException gdy opinia nie istnieje
     * @throws AccessDeniedException gdy wywołujący nie jest autorem opinii
     */
    @Transactional
    public void deleteReview(Integer reviewId, Integer callerId) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanej opinii"));
        if (!review.getStudent().getId().equals(callerId)) {
            throw new AccessDeniedException("Brak uprawnień do usunięcia tej opinii");
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Usuwa opinię przez administratora bez weryfikacji autorstwa.
     *
     * @param reviewId identyfikator opinii
     * @throws ResourceNotFoundException gdy opinia nie istnieje
     */
    @Transactional
    public void deleteReviewAdmin(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Nie znaleziono szukanej opinii");
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Zwraca wszystkie opinie wystawione danemu korepetytorowi.
     *
     * @param tutorId identyfikator korepetytora
     * @return lista opinii o korepetytorze
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getTutorReviews(Integer tutorId) {
        return reviewRepository.findByTutor_UserId(tutorId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    /**
     * Zwraca wszystkie opinie wystawione przez danego ucznia.
     *
     * @param studentId identyfikator ucznia
     * @return lista opinii ucznia
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getStudentReviews(Integer studentId) {
        return reviewRepository.findByStudent_Id(studentId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    /**
     * Wyszukuje opinie według frazy i opcjonalnej oceny.
     *
     * @param query fraza wyszukiwania (treść lub imię/nazwisko uczestnika)
     * @param rating filtr oceny (1–5)
     * @return lista pasujących opinii
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> searchReviews(String query, Integer rating) {
        BigDecimal ratingFilter = rating == null ? null : BigDecimal.valueOf(rating).setScale(1);
        return reviewRepository
                .searchReviews(SearchQueryUtils.toLikePattern(query), ratingFilter)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
