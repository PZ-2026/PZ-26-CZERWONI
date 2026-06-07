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

/**
 * Kontroler REST obsługujący endpointy opinii o korepetytorach.
 *
 * <p>Ścieżka bazowa: {@code /api/reviews}. Opinie korepetytora są publicznie dostępne. Dodawanie
 * opinii jest zarezerwowane dla uczniów (STUDENT), którzy odbyli lekcję z danym korepetytorem.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Zwraca wszystkie opinie wystawione danemu korepetytorowi. Publiczny endpoint.
     *
     * @param tutorId identyfikator korepetytora
     * @return lista opinii o korepetytorze
     */
    @GetMapping("/tutor/{tutorId}")
    public List<ReviewResponse> getTutorReviews(@PathVariable Integer tutorId) {
        return reviewService.getTutorReviews(tutorId);
    }

    /**
     * Zwraca opinie wystawione przez danego ucznia. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param studentId identyfikator ucznia
     * @return lista opinii ucznia
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #studentId")
    public List<ReviewResponse> getStudentReviews(@PathVariable Integer studentId) {
        return reviewService.getStudentReviews(studentId);
    }

    /**
     * Dodaje nową opinię korepetytorowi. Dostępne tylko dla STUDENT będącego właścicielem konta.
     *
     * @param studentId identyfikator ucznia wystawiającego opinię
     * @param request treść i ocena opinii
     * @return zapisana opinia
     */
    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT') and authentication.principal.id == #studentId")
    public ReviewResponse addReview(
            @PathVariable Integer studentId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.addReview(studentId, request);
    }

    /**
     * Aktualizuje istniejącą opinię. Autorstwo weryfikowane w serwisie.
     *
     * @param id identyfikator opinii
     * @param request nowa treść i ocena
     * @param currentUser aktualnie zalogowany użytkownik
     * @return zaktualizowana opinia
     */
    @PutMapping("/{id}")
    public ReviewResponse updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        return reviewService.updateReview(id, request, currentUser.getId());
    }

    /**
     * Usuwa opinię. Autorstwo weryfikowane w serwisie.
     *
     * @param id identyfikator opinii
     * @param currentUser aktualnie zalogowany użytkownik
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        reviewService.deleteReview(id, currentUser.getId());
    }
}
