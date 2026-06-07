package pl.edu.ur.teachly.lesson.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;
import pl.edu.ur.teachly.lesson.dto.request.AdminLessonUpdateRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.PaymentStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.StudentNotesRequest;
import pl.edu.ur.teachly.lesson.dto.request.TutorNotesRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.service.LessonService;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Kontroler REST obsługujący endpointy zarządzania lekcjami.
 *
 * <p>Ścieżka bazowa: {@code /api/lessons}. Dostęp do poszczególnych endpointów jest kontrolowany
 * przez adnotacje {@code @PreAuthorize} — niektóre operacje są zarezerwowane wyłącznie dla
 * administratora, inne dla uczestników lekcji.
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    /**
     * Zwraca listę wszystkich lekcji z opcjonalnym filtrowaniem. Dostępne tylko dla ADMIN.
     *
     * @param q fraza wyszukiwania
     * @param status filtr statusu lekcji
     * @param paymentStatus filtr statusu płatności
     * @param format filtr formatu lekcji
     * @param upcoming jeśli {@code true}, zwraca tylko przyszłe lekcje
     * @return lista lekcji spełniających kryteria
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<LessonResponse> getAllLessons(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) LessonStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) LessonFormat format,
            @RequestParam(required = false) Boolean upcoming) {
        return lessonService.searchLessons(q, status, paymentStatus, format, upcoming);
    }

    /**
     * Tworzy nową lekcję dla wskazanego ucznia. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param studentId identyfikator ucznia
     * @param request dane rezerwacji lekcji
     * @return szczegóły utworzonej lekcji
     */
    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(
            "hasRole('ADMIN') or (hasRole('STUDENT') and authentication.principal.id == #studentId)")
    public LessonResponse createLesson(
            @PathVariable Integer studentId, @Valid @RequestBody LessonRequest request) {
        return lessonService.createLesson(studentId, request);
    }

    /**
     * Zwraca wszystkie lekcje danego ucznia. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param studentId identyfikator ucznia
     * @return lista lekcji ucznia
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #studentId")
    public List<LessonResponse> getStudentLessons(@PathVariable Integer studentId) {
        return lessonService.getStudentLessons(studentId);
    }

    /**
     * Zwraca wszystkie lekcje danego korepetytora. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param tutorId identyfikator korepetytora
     * @return lista lekcji korepetytora
     */
    @GetMapping("/tutor/{tutorId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public List<LessonResponse> getTutorLessons(@PathVariable Integer tutorId) {
        return lessonService.getTutorLessons(tutorId);
    }

    /**
     * Zwraca szczegóły pojedynczej lekcji. Dostęp weryfikowany na poziomie serwisu.
     *
     * @param lessonId identyfikator lekcji
     * @return szczegóły lekcji
     */
    @GetMapping("/{lessonId}")
    public LessonResponse getLesson(@PathVariable Integer lessonId) {
        return lessonService.getLesson(lessonId);
    }

    /**
     * Aktualizuje dane lekcji przez administratora bez ograniczeń reguł biznesowych.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowe dane lekcji
     * @return zaktualizowane szczegóły lekcji
     */
    @PutMapping("/{lessonId}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse adminUpdateLesson(
            @PathVariable Integer lessonId, @Valid @RequestBody AdminLessonUpdateRequest request) {
        return lessonService.adminUpdateLesson(lessonId, request);
    }

    /**
     * Zmienia status lekcji. Przejścia stanów i uprawnienia są weryfikowane w serwisie.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowy status i opcjonalne notatki korepetytora
     * @return zaktualizowane szczegóły lekcji
     */
    @PatchMapping("/{lessonId}/status")
    public LessonResponse changeLessonStatus(
            @PathVariable Integer lessonId, @Valid @RequestBody LessonStatusRequest request) {
        return lessonService.changeLessonStatus(lessonId, request);
    }

    /**
     * Aktualizuje notatki ucznia dla wskazanej lekcji. Dostępne dla ADMIN lub STUDENT.
     *
     * @param lessonId identyfikator lekcji
     * @param request treść notatek
     * @param currentUser aktualnie zalogowany użytkownik
     * @return zaktualizowane szczegóły lekcji
     */
    @PatchMapping("/{lessonId}/student-notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public LessonResponse updateStudentNotes(
            @PathVariable Integer lessonId,
            @Valid @RequestBody StudentNotesRequest request,
            @AuthenticationPrincipal User currentUser) {
        return lessonService.updateStudentNotes(lessonId, request, currentUser.getId());
    }

    /**
     * Aktualizuje notatki korepetytora dla wskazanej lekcji. Dostępne dla ADMIN lub TUTOR.
     *
     * @param lessonId identyfikator lekcji
     * @param request treść notatek
     * @param currentUser aktualnie zalogowany użytkownik
     * @return zaktualizowane szczegóły lekcji
     */
    @PatchMapping("/{lessonId}/tutor-notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TUTOR')")
    public LessonResponse updateTutorNotes(
            @PathVariable Integer lessonId,
            @Valid @RequestBody TutorNotesRequest request,
            @AuthenticationPrincipal User currentUser) {
        return lessonService.updateTutorNotes(lessonId, request, currentUser.getId());
    }

    /**
     * Aktualizuje status płatności lekcji. Dostępne tylko dla ADMIN.
     *
     * @param lessonId identyfikator lekcji
     * @param request nowy status płatności
     * @return zaktualizowane szczegóły lekcji
     */
    @PatchMapping("/{lessonId}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse updatePaymentStatus(
            @PathVariable Integer lessonId, @Valid @RequestBody PaymentStatusRequest request) {
        return lessonService.updatePaymentStatus(lessonId, request);
    }
}
