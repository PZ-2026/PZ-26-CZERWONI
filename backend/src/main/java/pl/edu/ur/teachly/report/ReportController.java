package pl.edu.ur.teachly.report;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.ur.teachly.user.entity.User;

/**
 * Kontroler REST obsługujący generowanie raportów PDF.
 *
 * <p>Ścieżka bazowa: {@code /api/reports}. Każdy zalogowany użytkownik może wygenerować własny
 * raport — zakres dostępnych typów zależy od jego roli.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generuje i pobiera raport PDF dla zalogowanego użytkownika.
     *
     * @param startDate data początkowa zakresu (format ISO: yyyy-MM-dd)
     * @param endDate data końcowa zakresu (format ISO: yyyy-MM-dd)
     * @param type typ raportu (LESSONS, REVENUE, EXPENSES itp.); domyślnie LESSONS
     * @param includeFields lista pól do uwzględnienia; {@code null} oznacza wszystkie pola
     * @param user aktualnie zalogowany użytkownik
     * @return odpowiedź HTTP z plikiem PDF jako załącznikiem
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getMyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "LESSONS") String type,
            @RequestParam(required = false) List<String> includeFields,
            @AuthenticationPrincipal User user) {
        byte[] pdfBytes =
                reportService.generateReport(user, startDate, endDate, type, includeFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment", "raport_" + startDate + "_" + endDate + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
