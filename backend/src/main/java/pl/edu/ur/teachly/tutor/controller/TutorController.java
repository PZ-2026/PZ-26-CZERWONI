package pl.edu.ur.teachly.tutor.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSelfProfileRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorSubjectRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSearchResultResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.service.TutorService;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Kontroler REST obsługujący endpointy korepetytorów i ich oferty przedmiotowej.
 *
 * <p>Ścieżka bazowa: {@code /api/tutors}. Odczyt danych jest publiczny. Korepetytor może zarządzać
 * własnym profilem i przedmiotami (rola TUTOR), a administrator ma pełny dostęp administracyjny
 * (rola ADMIN).
 */
@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorController {
    private final TutorService tutorService;

    /**
     * Wyszukuje aktywnych korepetytorów z ocenami i listą przedmiotów.
     *
     * @param q fraza wyszukiwania (imię, nazwisko)
     * @param subject filtr po nazwie przedmiotu
     * @return lista wyników z ocenami i przedmiotami
     */
    @GetMapping("/search")
    public List<TutorSearchResultResponse> searchTutors(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String subject) {
        return tutorService.searchTutors(q, subject);
    }

    /**
     * Zwraca listę wszystkich aktywnych korepetytorów. Publiczny endpoint.
     *
     * @param q opcjonalna fraza wyszukiwania
     * @return lista korepetytorów
     */
    @GetMapping
    public List<TutorResponse> getAllTutors(@RequestParam(required = false) String q) {
        return tutorService.getAllTutors(q);
    }

    /**
     * Zwraca profil aktywnego korepetytora. Publiczny endpoint.
     *
     * @param id identyfikator korepetytora
     * @return dane korepetytora
     */
    @GetMapping("/{id}")
    public TutorResponse getTutorById(@PathVariable Integer id) {
        return tutorService.getTutorById(id);
    }

    /**
     * Zwraca listę przedmiotów prowadzonych przez korepetytora. Publiczny endpoint.
     *
     * @param id identyfikator korepetytora
     * @return lista przedmiotów z poziomami nauczania
     */
    @GetMapping("/{id}/subjects")
    public List<TutorSubjectResponse> getTutorSubjects(@PathVariable Integer id) {
        return tutorService.getTutorSubjects(id);
    }

    /**
     * Aktualizuje własny profil zalogowanego korepetytora. Dostępne tylko dla TUTOR.
     *
     * @param request nowe dane profilu
     * @param currentUser aktualnie zalogowany korepetytor
     * @return zaktualizowany profil
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('TUTOR')")
    public TutorResponse updateMyProfile(
            @Valid @RequestBody TutorSelfProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        return tutorService.updateMyProfile(request, currentUser);
    }

    /**
     * Dodaje przedmiot do własnej oferty zalogowanego korepetytora. Dostępne tylko dla TUTOR.
     *
     * @param request dane przedmiotu i poziomów nauczania
     * @param currentUser aktualnie zalogowany korepetytor
     * @return dodany przedmiot
     */
    @PostMapping("/me/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('TUTOR')")
    public TutorSubjectResponse addMySubject(
            @Valid @RequestBody TutorSubjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return tutorService.addMySubject(request, currentUser);
    }

    /**
     * Usuwa przedmiot z własnej oferty zalogowanego korepetytora. Dostępne tylko dla TUTOR.
     *
     * @param id identyfikator wpisu przedmiotu
     * @param currentUser aktualnie zalogowany korepetytor
     */
    @DeleteMapping("/me/subjects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('TUTOR')")
    public void removeMySubject(
            @PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        tutorService.removeMySubject(id, currentUser);
    }

    /**
     * Aktualizuje profil korepetytora przez administratora. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator korepetytora
     * @param request nowe dane profilu
     * @return zaktualizowany profil
     */
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public TutorResponse adminUpdateTutor(
            @PathVariable Integer id, @Valid @RequestBody TutorRequest request) {
        return tutorService.adminUpdateTutor(id, request);
    }

    /**
     * Dodaje przedmiot do oferty korepetytora przez administratora. Dostępne tylko dla ADMIN.
     *
     * @param tutorId identyfikator korepetytora
     * @param request dane przedmiotu i poziomów nauczania
     * @return dodany przedmiot
     */
    @PostMapping("/{tutorId}/admin/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TutorSubjectResponse adminAddSubject(
            @PathVariable Integer tutorId, @Valid @RequestBody TutorSubjectRequest request) {
        return tutorService.adminAddSubject(tutorId, request);
    }

    /**
     * Usuwa przedmiot z oferty korepetytora przez administratora. Dostępne tylko dla ADMIN.
     *
     * @param tutorId identyfikator korepetytora
     * @param tutorSubjectId identyfikator wpisu przedmiotu
     */
    @DeleteMapping("/{tutorId}/admin/subjects/{tutorSubjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void adminRemoveSubject(
            @PathVariable Integer tutorId, @PathVariable Integer tutorSubjectId) {
        tutorService.adminRemoveSubject(tutorId, tutorSubjectId);
    }
}
