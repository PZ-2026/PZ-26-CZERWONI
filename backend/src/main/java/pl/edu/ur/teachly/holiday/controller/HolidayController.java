package pl.edu.ur.teachly.holiday.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.service.HolidayService;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    public List<HolidayResponse> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponse addHoliday(@Valid @RequestBody HolidayRequest request) {
        return holidayService.addHoliday(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HolidayResponse updateHoliday(
            @PathVariable Integer id, @Valid @RequestBody HolidayRequest request) {
        return holidayService.updateHoliday(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHoliday(@PathVariable Integer id) {
        holidayService.deleteHoliday(id);
    }
}
