package pl.edu.ur.teachly.admin.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.admin.dto.response.AdminStatsResponse;
import pl.edu.ur.teachly.admin.service.AdminService;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.service.ReviewService;

/**
 * Kontroler REST panelu administracyjnego.
 *
 * <p>Ścieżka bazowa: {@code /api/admin}. Wszystkie endpointy tej klasy są dostępne wyłącznie dla
 * użytkowników z rolą ADMIN (adnotacja na poziomie klasy).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final ReviewService reviewService;

    /**
     * Zwraca zagregowane statystyki systemu (użytkownicy, lekcje, przedmioty itp.).
     *
     * @return obiekt ze statystykami systemu
     */
    @GetMapping("/stats")
    public AdminStatsResponse getStats() {
        return adminService.getStats();
    }

    /**
     * Zwraca listę opinii z opcjonalnym filtrowaniem.
     *
     * @param q fraza wyszukiwania (treść opinii lub imię/nazwisko)
     * @param rating filtr oceny (1–5)
     * @return lista opinii spełniających kryteria
     */
    @GetMapping("/reviews")
    public List<ReviewResponse> getAllReviews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer rating) {
        return reviewService.searchReviews(q, rating);
    }

    /**
     * Usuwa opinię o podanym identyfikatorze.
     *
     * @param id identyfikator opinii do usunięcia
     */
    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Integer id) {
        reviewService.deleteReviewAdmin(id);
    }
}
