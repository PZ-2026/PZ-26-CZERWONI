package pl.edu.ur.teachly.lesson.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.lesson.dto.request.*;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LessonResponse createLesson(@PathVariable Integer studentId, @Valid @RequestBody LessonRequest request) {
        return lessonService.createLesson(studentId, request);
    }

    @GetMapping("/student/{studentId}")
    public List<LessonResponse> getStudentLessons(@PathVariable Integer studentId) {
        return lessonService.getStudentLessons(studentId);
    }

    @GetMapping("/tutor/{tutorId}")
    public List<LessonResponse> getTutorLessons(@PathVariable Integer tutorId) {
        return lessonService.getTutorLessons(tutorId);
    }

    @GetMapping("/{lessonId}")
    public LessonResponse getLesson(@PathVariable Integer lessonId) {
        return lessonService.getLesson(lessonId);
    }

    @PatchMapping("/{lessonId}/status")
    public LessonResponse changeLessonStatus(@PathVariable Integer lessonId, @Valid @RequestBody LessonStatusRequest request) {
        return lessonService.changeLessonStatus(lessonId, request);
    }

    @PatchMapping("/{lessonId}/student-notes")
    public LessonResponse updateStudentNotes(@PathVariable Integer lessonId, @RequestBody StudentNotesRequest request) {
        return lessonService.updateStudentNotes(lessonId, request);
    }

    @PatchMapping("/{lessonId}/tutor-notes")
    public LessonResponse updateTutorNotes(@PathVariable Integer lessonId, @RequestBody TutorNotesRequest request) {
        return lessonService.updateTutorNotes(lessonId, request);
    }

    @PatchMapping("/{lessonId}/payment")
    public LessonResponse updatePaymentStatus(@PathVariable Integer lessonId, @Valid @RequestBody PaymentStatusRequest request) {
        return lessonService.updatePaymentStatus(lessonId, request);
    }
}
