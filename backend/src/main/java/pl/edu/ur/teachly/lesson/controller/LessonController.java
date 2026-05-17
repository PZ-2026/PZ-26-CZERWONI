package pl.edu.ur.teachly.lesson.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.lesson.dto.request.AdminLessonUpdateRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.PaymentStatusRequest;
import pl.edu.ur.teachly.lesson.dto.request.StudentNotesRequest;
import pl.edu.ur.teachly.lesson.dto.request.TutorNotesRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.service.LessonService;
import pl.edu.ur.teachly.user.entity.User;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<LessonResponse> getAllLessons() {
        return lessonService.getAllLessons();
    }

    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(
            "hasRole('ADMIN') or (hasRole('STUDENT') and authentication.principal.id == #studentId)")
    public LessonResponse createLesson(
            @PathVariable Integer studentId, @Valid @RequestBody LessonRequest request) {
        return lessonService.createLesson(studentId, request);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #studentId")
    public List<LessonResponse> getStudentLessons(@PathVariable Integer studentId) {
        return lessonService.getStudentLessons(studentId);
    }

    @GetMapping("/tutor/{tutorId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #tutorId")
    public List<LessonResponse> getTutorLessons(@PathVariable Integer tutorId) {
        return lessonService.getTutorLessons(tutorId);
    }

    @GetMapping("/{lessonId}")
    public LessonResponse getLesson(@PathVariable Integer lessonId) {
        return lessonService.getLesson(lessonId);
    }

    @PutMapping("/{lessonId}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse adminUpdateLesson(
            @PathVariable Integer lessonId, @Valid @RequestBody AdminLessonUpdateRequest request) {
        return lessonService.adminUpdateLesson(lessonId, request);
    }

    @PatchMapping("/{lessonId}/status")
    public LessonResponse changeLessonStatus(
            @PathVariable Integer lessonId, @Valid @RequestBody LessonStatusRequest request) {
        return lessonService.changeLessonStatus(lessonId, request);
    }

    @PatchMapping("/{lessonId}/student-notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public LessonResponse updateStudentNotes(
            @PathVariable Integer lessonId,
            @Valid @RequestBody StudentNotesRequest request,
            @AuthenticationPrincipal User currentUser) {
        return lessonService.updateStudentNotes(lessonId, request, currentUser.getId());
    }

    @PatchMapping("/{lessonId}/tutor-notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TUTOR')")
    public LessonResponse updateTutorNotes(
            @PathVariable Integer lessonId,
            @Valid @RequestBody TutorNotesRequest request,
            @AuthenticationPrincipal User currentUser) {
        return lessonService.updateTutorNotes(lessonId, request, currentUser.getId());
    }

    @PatchMapping("/{lessonId}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse updatePaymentStatus(
            @PathVariable Integer lessonId, @Valid @RequestBody PaymentStatusRequest request) {
        return lessonService.updatePaymentStatus(lessonId, request);
    }
}
