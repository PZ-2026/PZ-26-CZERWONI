package pl.edu.ur.teachly.subject.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.subject.dto.request.SubjectCategoryRequest;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectCategoryResponse;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.service.SubjectService;

/**
 * Kontroler REST obsługujący endpointy przedmiotów i ich kategorii.
 *
 * <p>Ścieżka bazowa: {@code /api/subjects}. Odczyt danych jest publiczny, natomiast operacje zapisu
 * (dodawanie, edycja, usuwanie) wymagają roli ADMIN.
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    /**
     * Zwraca listę wszystkich przedmiotów. Publiczny endpoint.
     *
     * @return lista przedmiotów
     */
    @GetMapping
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    /**
     * Dodaje nowy przedmiot. Dostępne tylko dla ADMIN.
     *
     * @param request dane nowego przedmiotu
     * @return zapisany przedmiot
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse addSubject(@Valid @RequestBody SubjectRequest request) {
        return subjectService.addSubject(request);
    }

    /**
     * Aktualizuje dane przedmiotu. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator przedmiotu
     * @param request nowe dane przedmiotu
     * @return zaktualizowany przedmiot
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse updateSubject(
            @PathVariable Integer id, @Valid @RequestBody SubjectRequest request) {
        return subjectService.updateSubject(id, request);
    }

    /**
     * Usuwa przedmiot. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator przedmiotu
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
    }

    /**
     * Zwraca listę wszystkich kategorii przedmiotów. Publiczny endpoint.
     *
     * @return lista kategorii
     */
    @GetMapping("/categories")
    public List<SubjectCategoryResponse> getAllCategories() {
        return subjectService.getAllCategories();
    }

    /**
     * Dodaje nową kategorię przedmiotów. Dostępne tylko dla ADMIN.
     *
     * @param request dane nowej kategorii
     * @return zapisana kategoria
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectCategoryResponse addSubjectCategory(
            @Valid @RequestBody SubjectCategoryRequest request) {
        return subjectService.addSubjectCategory(request);
    }

    /**
     * Aktualizuje kategorię przedmiotów. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator kategorii
     * @param request nowa nazwa kategorii
     * @return zaktualizowana kategoria
     */
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectCategoryResponse updateSubjectCategory(
            @PathVariable Integer id, @Valid @RequestBody SubjectCategoryRequest request) {
        return subjectService.updateSubjectCategory(id, request);
    }

    /**
     * Usuwa kategorię przedmiotów. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator kategorii
     */
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubjectCategory(@PathVariable Integer id) {
        subjectService.deleteSubjectCategory(id);
    }
}
