package pl.edu.ur.teachly.tutor.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.tutor.dto.request.TutorRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.service.TutorService;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorController {
    private final TutorService tutorService;

    @GetMapping
    public List<TutorResponse> getAllTutors() {
        return tutorService.getAllTutors();
    }

    @GetMapping("/{id}")
    public TutorResponse getTutorById(@PathVariable Integer id) {
        return tutorService.getTutorById(id);
    }

    @GetMapping("/{id}/subjects")
    public List<TutorSubjectResponse> getTutorSubjects(@PathVariable Integer id) {
        return tutorService.getTutorSubjects(id);
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public TutorResponse adminUpdateTutor(
            @PathVariable Integer id, @Valid @RequestBody TutorRequest request) {
        return tutorService.adminUpdateTutor(id, request);
    }
}
