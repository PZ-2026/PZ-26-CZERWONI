package pl.edu.ur.teachly.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.entity.Review;
import pl.edu.ur.teachly.review.mapper.ReviewMapper;
import pl.edu.ur.teachly.review.repository.ReviewRepository;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final LessonRepository lessonRepository;

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

        // Validation if user has a completed lesson with this tutor
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

    @Transactional
    public void deleteReviewAdmin(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Nie znaleziono szukanej opinii");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getTutorReviews(Integer tutorId) {
        return reviewRepository.findByTutor_UserId(tutorId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getStudentReviews(Integer studentId) {
        return reviewRepository.findByStudent_Id(studentId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream().map(reviewMapper::toResponse).toList();
    }
}
