package pl.edu.ur.teachly.holiday.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.holiday.dto.request.HolidayRequest;
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.service.HolidayService;

/**
 * Kontroler REST obsługujący endpointy zarządzania dniami wolnymi.
 *
 * <p>Ścieżka bazowa: {@code /api/holidays}. Pobieranie listy dni wolnych jest publicznie dostępne.
 * Operacje zapisu wymagają roli ADMIN.
 */
@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;

    /**
     * Zwraca listę wszystkich dni wolnych. Publiczny endpoint.
     *
     * @return lista dni wolnych
     */
    @GetMapping
    public List<HolidayResponse> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    /**
     * Dodaje nowy dzień wolny. Dostępne tylko dla ADMIN.
     *
     * @param request dane nowego dnia wolnego
     * @return zapisany dzień wolny
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponse addHoliday(@Valid @RequestBody HolidayRequest request) {
        return holidayService.addHoliday(request);
    }

    /**
     * Aktualizuje istniejący dzień wolny. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator dnia wolnego
     * @param request nowe dane dnia wolnego
     * @return zaktualizowany dzień wolny
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HolidayResponse updateHoliday(
            @PathVariable Integer id, @Valid @RequestBody HolidayRequest request) {
        return holidayService.updateHoliday(id, request);
    }

    /**
     * Usuwa dzień wolny. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator dnia wolnego
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHoliday(@PathVariable Integer id) {
        holidayService.deleteHoliday(id);
    }
}
