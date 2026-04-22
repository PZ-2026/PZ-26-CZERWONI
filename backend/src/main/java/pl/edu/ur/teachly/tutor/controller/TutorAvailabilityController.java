package pl.edu.ur.teachly.tutor.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/tutors/{tutorId}/availability")
@RequiredArgsConstructor
public class TutorAvailabilityController {
    private final TutorAvailabilityService availabilityService;
    private final TimetableService timetableService;

    @GetMapping("/timetable")
    public List<TimetableDayResponse> getTimetable(
            @PathVariable Integer tutorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User currentUser) {
        Integer studentId = currentUser != null ? currentUser.getId() : null;
        return timetableService.getTimetable(tutorId, from, to, studentId);
    }

    @GetMapping("/recurring")
    public List<TutorAvailabilityRecurringResponse> getRecurringByTutor(
            @PathVariable Integer tutorId) {
        return availabilityService.getRecurringByTutor(tutorId);
    }

    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    public TutorAvailabilityRecurringResponse addRecurring(
            @PathVariable Integer tutorId,
            @Valid @RequestBody TutorAvailabilityRecurringRequest request) {
        return availabilityService.addRecurring(tutorId, request);
    }

    @DeleteMapping("/recurring/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecurring(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteRecurring(id);
    }

    @GetMapping("/override")
    public List<TutorAvailabilityOverrideResponse> getOverridesByTutor(
            @PathVariable Integer tutorId) {
        return availabilityService.getOverridesByTutor(tutorId);
    }

    @PostMapping("/override")
    @ResponseStatus(HttpStatus.CREATED)
    public TutorAvailabilityOverrideResponse addOverride(
            @PathVariable Integer tutorId,
            @Valid @RequestBody TutorAvailabilityOverrideRequest request) {
        return availabilityService.addOverride(tutorId, request);
    }

    @DeleteMapping("/override/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOverride(@PathVariable Integer tutorId, @PathVariable Integer id) {
        availabilityService.deleteOverride(id);
    }
}
