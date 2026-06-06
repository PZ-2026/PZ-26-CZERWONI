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

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorController {
    private final TutorService tutorService;

    @GetMapping("/search")
    public List<TutorSearchResultResponse> searchTutors(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String subject) {
        return tutorService.searchTutors(q, subject);
    }

    @GetMapping
    public List<TutorResponse> getAllTutors(@RequestParam(required = false) String q) {
        return tutorService.getAllTutors(q);
    }

    @GetMapping("/{id}")
    public TutorResponse getTutorById(@PathVariable Integer id) {
        return tutorService.getTutorById(id);
    }

    @GetMapping("/{id}/subjects")
    public List<TutorSubjectResponse> getTutorSubjects(@PathVariable Integer id) {
        return tutorService.getTutorSubjects(id);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('TUTOR')")
    public TutorResponse updateMyProfile(
            @Valid @RequestBody TutorSelfProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        return tutorService.updateMyProfile(request, currentUser);
    }

    @PostMapping("/me/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('TUTOR')")
    public TutorSubjectResponse addMySubject(
            @Valid @RequestBody TutorSubjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return tutorService.addMySubject(request, currentUser);
    }

    @DeleteMapping("/me/subjects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('TUTOR')")
    public void removeMySubject(
            @PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        tutorService.removeMySubject(id, currentUser);
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public TutorResponse adminUpdateTutor(
            @PathVariable Integer id, @Valid @RequestBody TutorRequest request) {
        return tutorService.adminUpdateTutor(id, request);
    }

    @PostMapping("/{tutorId}/admin/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TutorSubjectResponse adminAddSubject(
            @PathVariable Integer tutorId, @Valid @RequestBody TutorSubjectRequest request) {
        return tutorService.adminAddSubject(tutorId, request);
    }

    @DeleteMapping("/{tutorId}/admin/subjects/{tutorSubjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void adminRemoveSubject(
            @PathVariable Integer tutorId, @PathVariable Integer tutorSubjectId) {
        tutorService.adminRemoveSubject(tutorId, tutorSubjectId);
    }
}
