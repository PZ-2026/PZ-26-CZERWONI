package pl.edu.ur.teachly.subject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.subject.dto.request.SubjectCategoryRequest;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectCategoryResponse;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse addSubject(@Valid @RequestBody SubjectRequest request) {
        return subjectService.addSubject(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse updateSubject(
            @PathVariable Integer id, @Valid @RequestBody SubjectRequest request) {
        return subjectService.updateSubject(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
    }

    @GetMapping("/categories")
    public List<SubjectCategoryResponse> getAllCategories() {
        return subjectService.getAllCategories();
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectCategoryResponse addSubjectCategory(
            @Valid @RequestBody SubjectCategoryRequest request) {
        return subjectService.addSubjectCategory(request);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectCategoryResponse updateSubjectCategory(
            @PathVariable Integer id, @Valid @RequestBody SubjectCategoryRequest request) {
        return subjectService.updateSubjectCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubjectCategory(@PathVariable Integer id) {
        subjectService.deleteSubjectCategory(id);
    }
}
