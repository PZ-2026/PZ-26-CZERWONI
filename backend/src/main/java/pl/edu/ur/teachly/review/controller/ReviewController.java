package pl.edu.ur.teachly.review.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.service.ReviewService;
import pl.edu.ur.teachly.user.entity.User;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/tutor/{tutorId}")
    public List<ReviewResponse> getTutorReviews(@PathVariable Integer tutorId) {
        return reviewService.getTutorReviews(tutorId);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #studentId")
    public List<ReviewResponse> getStudentReviews(@PathVariable Integer studentId) {
        return reviewService.getStudentReviews(studentId);
    }

    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT') and authentication.principal.id == #studentId")
    public ReviewResponse addReview(
            @PathVariable Integer studentId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.addReview(studentId, request);
    }

    @PutMapping("/{id}")
    public ReviewResponse updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        return reviewService.updateReview(id, request, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        reviewService.deleteReview(id, currentUser.getId());
    }
}
