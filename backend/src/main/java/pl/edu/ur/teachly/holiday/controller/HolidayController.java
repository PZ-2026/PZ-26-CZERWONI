package pl.edu.ur.teachly.holiday.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.service.HolidayService;

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
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponse addHoliday(@Valid @RequestBody HolidayRequest request) {
        return holidayService.addHoliday(request);
    }

    @PutMapping("/{id}")
    public HolidayResponse updateHoliday(
            @PathVariable Integer id, @Valid @RequestBody HolidayRequest request) {
        return holidayService.updateHoliday(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHoliday(@PathVariable Integer id) {
        holidayService.deleteHoliday(id);
    }
}
