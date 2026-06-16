package pl.edu.ur.teachly.tutor.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityOverrideRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityRecurringRequest;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityOverrideResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityRecurringResponse;
import pl.edu.ur.teachly.tutor.service.TimetableService;
import pl.edu.ur.teachly.tutor.service.TutorAvailabilityService;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Kontroler REST obsługujący dostępność korepetytora i jego plan zajęć.
 *
 * <p>Ścieżka bazowa: {@code /api/tutors/{tutorId}/availability}. Odczyt danych jest publiczny.
 * Modyfikacja dostępności (dodawanie, usuwanie wpisów) wymaga roli ADMIN lub bycia właścicielem
 * konta korepetytora.
 */
@RestController
@RequestMapping("/api/tutors/{tutorId}/availability")
@RequiredArgsConstructor
public class TutorAvailabilityController {
    private final TutorAvailabilityService availabilityService;
    private final TimetableService timetableService;

    /**
     * Zwraca plan wolnych terminów korepetytora w podanym zakresie dat.
     *
     * @param tutorId identyfikator korepetytora
     * @param from data początkowa zakresu (format ISO: yyyy-MM-dd)
     * @param to data końcowa zakresu (format ISO: yyyy-MM-dd)
     * @param currentUser aktualnie zalogowany użytkownik (może być {@code null})
     * @return lista dni z dostępnymi slotami godzinowymi
     */
    @GetMapping("/timetable")
    public List<TimetableDayResponse> getTimetable(
            @PathVariable Integer tutorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User currentUser) {
        Integer studentId = currentUser != null ? currentUser.getId() : null;
        return timetableService.getTimetable(tutorId, from, to, studentId);
    }

    /**
     * Zwraca cykliczne sloty dostępności korepetytora.
     *
     * @param tutorId identyfikator korepetytora
     * @return lista wpisów cyklicznej dostępności
     */
    @GetMapping("/recurring")
    public List<TutorAvailabilityRecurringResponse> getRecurringByTutor(
            @PathVariable Integer tutorId) {
        return availabilityService.getRecurringByTutor(tutorId);
    }

    /**
     * Dodaje nowy cykliczny slot dostępności. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param tutorId identyfikator korepetytora
     * @param request dane nowego slotu cyklicznego
     * @return zapisany slot
     */
    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public TutorAvailabilityRecurringResponse addRecurring(
            @PathVariable Integer tutorId,
            @Valid @RequestBody TutorAvailabilityRecurringRequest request) {
        return availabilityService.addRecurring(tutorId, request);
    }

    /**
     * Usuwa cykliczny slot dostępności. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param tutorId identyfikator korepetytora
     * @param id identyfikator slotu cyklicznego
     */
    @DeleteMapping("/recurring/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public void deleteRecurring(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteRecurring(id, tutorId);
    }

    /**
     * Zwraca jednorazowe nadpisania dostępności korepetytora.
     *
     * @param tutorId identyfikator korepetytora
     * @return lista nadpisań dostępności
     */
    @GetMapping("/override")
    public List<TutorAvailabilityOverrideResponse> getOverridesByTutor(
            @PathVariable Integer tutorId) {
        return availabilityService.getOverridesByTutor(tutorId);
    }

    /**
     * Dodaje jednorazowe nadpisanie dostępności. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param tutorId identyfikator korepetytora
     * @param request dane nadpisania
     * @return zapisane nadpisanie
     */
    @PostMapping("/override")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public TutorAvailabilityOverrideResponse addOverride(
            @PathVariable Integer tutorId,
            @Valid @RequestBody TutorAvailabilityOverrideRequest request) {
        return availabilityService.addOverride(tutorId, request);
    }

    /**
     * Usuwa jednorazowe nadpisanie dostępności. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param tutorId identyfikator korepetytora
     * @param id identyfikator nadpisania
     */
    @DeleteMapping("/override/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public void deleteOverride(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteOverride(id, tutorId);
    }
}
