package pl.edu.ur.teachly.tutor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.service.TutorService;

import java.util.List;

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
}
