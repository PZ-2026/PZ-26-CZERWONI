package pl.edu.ur.teachly.tutor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityOverrideRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityRecurringRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityOverrideResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityRecurringResponse;
import pl.edu.ur.teachly.tutor.service.TutorAvailabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/tutors/{tutorId}/availability")
@RequiredArgsConstructor
public class TutorAvailabilityController {
    private final TutorAvailabilityService availabilityService;

    @GetMapping("/recurring")
    public List<TutorAvailabilityRecurringResponse> getRecurringByTutor(@PathVariable Integer tutorId) {
        return availabilityService.getRecurringByTutor(tutorId);
    }

    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    public TutorAvailabilityRecurringResponse addRecurring(@PathVariable Integer tutorId, @Valid @RequestBody TutorAvailabilityRecurringRequest request) {
        return availabilityService.addRecurring(tutorId, request);
    }

    @DeleteMapping("/recurring/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecurring(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteRecurring(id);
    }

    @GetMapping("/override")
    public List<TutorAvailabilityOverrideResponse> getOverridesByTutor(@PathVariable Integer tutorId) {
        return availabilityService.getOverridesByTutor(tutorId);
    }

    @PostMapping("/override")
    @ResponseStatus(HttpStatus.CREATED)
    public TutorAvailabilityOverrideResponse addOverride(@PathVariable Integer tutorId, @Valid @RequestBody TutorAvailabilityOverrideRequest request) {
        return availabilityService.addOverride(tutorId, request);
    }

    @DeleteMapping("/override/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOverride(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteOverride(id);
    }
}
