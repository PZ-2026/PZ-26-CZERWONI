package pl.edu.ur.teachly.report;

import com.lowagie.text.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.report.pdf.PdfDocumentBuilder;
import pl.edu.ur.teachly.report.pdf.PdfTableBuilder;
import pl.edu.ur.teachly.user.entity.User;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LessonRepository lessonRepository;

    @Transactional(readOnly = true)
    public byte[] generateReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Lesson> lessons;
        if (user.getUserRole() == UserRole.ADMIN) {
            lessons = lessonRepository.findByLessonDateBetween(startDate, endDate);
        } else if (user.getUserRole() == UserRole.TUTOR) {
            lessons =
                    lessonRepository.findByTutor_UserIdAndLessonDateBetween(
                            user.getId(), startDate, endDate);
        } else {
            lessons =
                    lessonRepository.findByStudent_IdAndLessonDateBetween(
                            user.getId(), startDate, endDate);
        }

        // Sort by date (oldest first) and then by time
        lessons.sort(java.util.Comparator.comparing(Lesson::getLessonDate)
                .thenComparing(Lesson::getTimeFrom));

        if (user.getUserRole() == UserRole.ADMIN) {
            return buildAdminReport(lessons, startDate, endDate);
        } else if (user.getUserRole() == UserRole.TUTOR) {
            return buildTutorReport(user, lessons, startDate, endDate);
        } else {
            return buildStudentReport(user, lessons, startDate, endDate);
        }
    }

    private String statusLabel(LessonStatus status) {
        return switch (status) {
            case COMPLETED -> "Odbyta";
            case CANCELLED -> "Anulowana";
            case CONFIRMED -> "Potwierdzona";
            case PENDING -> "Oczekuj\u0105ca";
        };
    }

    /** "Dzień: 2026-05-13" dla jednego dnia, "Okres: X – Y" dla zakresu */
    private String periodLabel(LocalDate startDate, LocalDate endDate) {
        if (startDate.equals(endDate)) {
            return "Dzień: " + startDate;
        }
        return "Okres: " + startDate + " do " + endDate;
    }

    private Font coloredFont(float size, java.awt.Color color) {
        return new Font(PdfDocumentBuilder.BASE_FONT, size, Font.NORMAL, color);
    }

    private byte[] buildAdminReport(List<Lesson> lessons, LocalDate startDate, LocalDate endDate) {
        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());
        List<Lesson> cancelled =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CANCELLED)
                        .collect(Collectors.toList());
        List<Lesson> confirmed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CONFIRMED)
                        .collect(Collectors.toList());
        List<Lesson> pending =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.PENDING)
                        .collect(Collectors.toList());

        BigDecimal totalRevenue =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalMinutes =
                completed.stream()
                        .mapToLong(
                                l ->
                                        java.time.Duration.between(l.getTimeFrom(), l.getTimeTo())
                                                .toMinutes())
                        .sum();

        PdfDocumentBuilder docBuilder =
                new PdfDocumentBuilder()
                        .addTitle("Raport Administratora")
                        .addSubtitle(periodLabel(startDate, endDate));

        docBuilder.addParagraph("Podsumowanie platformy:");
        docBuilder.addParagraph("Wszystkich lekcji: " + lessons.size());
        docBuilder.addParagraph(
                "Odbytych: " + completed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_COMPLETED));
        docBuilder.addParagraph(
                "Anulowanych: " + cancelled.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CANCELLED));
        docBuilder.addParagraph(
                "Potwierdzonych: " + confirmed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CONFIRMED));
        docBuilder.addParagraph(
                "Oczekuj\u0105cych: " + pending.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_PENDING));
        docBuilder.addParagraph("\u0141\u0105czny obr\u00f3t (odbyte): " + totalRevenue + " PLN");
        docBuilder.addParagraph(
                "Łączna liczba godzin odbytych: "
                        + String.format("%.1f", totalMinutes / 60.0)
                        + " h");
        docBuilder.addParagraph(" ");

        PdfTableBuilder tableBuilder = new PdfTableBuilder(new float[] {2, 2, 3, 3, 2, 2, 2});
        tableBuilder.addHeaders("Data", "Czas", "Korepetytor", "Ucze\u0144", "Przedmiot", "Status", "Kwota");

        for (Lesson l : lessons) {
            tableBuilder.addRowWithStatus(
                    l.getLessonStatus().name(),
                    l.getLessonDate().toString(),
                    l.getTimeFrom() + " - " + l.getTimeTo(),
                    l.getTutor().getUser().getFirstName()
                            + " "
                            + l.getTutor().getUser().getLastName(),
                    l.getStudent().getFirstName() + " " + l.getStudent().getLastName(),
                    l.getSubject().getSubjectName(),
                    statusLabel(l.getLessonStatus()),
                    l.getAmount() + " PLN");
        }

        return docBuilder.addTable(tableBuilder.build()).build();
    }

    private byte[] buildTutorReport(
            User user, List<Lesson> lessons, LocalDate startDate, LocalDate endDate) {

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());
        List<Lesson> cancelled =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CANCELLED)
                        .collect(Collectors.toList());
        List<Lesson> confirmed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CONFIRMED)
                        .collect(Collectors.toList());
        List<Lesson> pending =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.PENDING)
                        .collect(Collectors.toList());

        BigDecimal totalEarnings =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalMinutes =
                completed.stream()
                        .mapToLong(
                                l ->
                                        java.time.Duration.between(l.getTimeFrom(), l.getTimeTo())
                                                .toMinutes())
                        .sum();

        PdfDocumentBuilder docBuilder =
                new PdfDocumentBuilder()
                        .addTitle("Raport Korepetytora")
                        .addSubtitle(
                                user.getFirstName()
                                        + " "
                                        + user.getLastName()
                                        + "  |  "
                                        + periodLabel(startDate, endDate));

        docBuilder.addParagraph("Podsumowanie:");
        docBuilder.addParagraph("Wszystkich lekcji: " + lessons.size());
        docBuilder.addParagraph(
                "Odbytych: " + completed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_COMPLETED));
        docBuilder.addParagraph(
                "Anulowanych: " + cancelled.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CANCELLED));
        docBuilder.addParagraph("Potwierdzonych: " + confirmed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CONFIRMED));
        docBuilder.addParagraph(
                "Oczekuj\u0105cych: " + pending.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_PENDING));
        docBuilder.addParagraph(
                "Przepracowane godziny: " + String.format("%.1f", totalMinutes / 60.0) + " h");
        docBuilder.addParagraph("\u0141\u0105czne zarobki (odbyte): " + totalEarnings + " PLN");
        docBuilder.addParagraph(" ");

        PdfTableBuilder tableBuilder = new PdfTableBuilder(new float[] {2, 2, 3, 2, 2, 2});
        tableBuilder.addHeaders("Data", "Czas", "Ucze\u0144", "Przedmiot", "Status", "Kwota");

        for (Lesson l : lessons) {
            tableBuilder.addRowWithStatus(
                    l.getLessonStatus().name(),
                    l.getLessonDate().toString(),
                    l.getTimeFrom() + " - " + l.getTimeTo(),
                    l.getStudent().getFirstName() + " " + l.getStudent().getLastName(),
                    l.getSubject().getSubjectName(),
                    statusLabel(l.getLessonStatus()),
                    l.getAmount() + " PLN");
        }

        return docBuilder.addTable(tableBuilder.build()).build();
    }

    private byte[] buildStudentReport(
            User user, List<Lesson> lessons, LocalDate startDate, LocalDate endDate) {

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());
        List<Lesson> cancelled =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CANCELLED)
                        .collect(Collectors.toList());
        List<Lesson> confirmed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.CONFIRMED)
                        .collect(Collectors.toList());
        List<Lesson> pending =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.PENDING)
                        .collect(Collectors.toList());

        BigDecimal totalSpent =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalMinutes =
                completed.stream()
                        .mapToLong(
                                l ->
                                        java.time.Duration.between(l.getTimeFrom(), l.getTimeTo())
                                                .toMinutes())
                        .sum();

        PdfDocumentBuilder docBuilder =
                new PdfDocumentBuilder()
                        .addTitle("Raport Ucznia")
                        .addSubtitle(
                                user.getFirstName()
                                        + " "
                                        + user.getLastName()
                                        + "  |  "
                                        + periodLabel(startDate, endDate));

        docBuilder.addParagraph("Podsumowanie:");
        docBuilder.addParagraph("Wszystkich lekcji: " + lessons.size());
        docBuilder.addParagraph(
                "Odbytych: " + completed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_COMPLETED));
        docBuilder.addParagraph(
                "Anulowanych: " + cancelled.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CANCELLED));
        docBuilder.addParagraph("Potwierdzonych: " + confirmed.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_CONFIRMED));
        docBuilder.addParagraph(
                "Oczekuj\u0105cych: " + pending.size(),
                coloredFont(11, PdfTableBuilder.COLOR_TEXT_PENDING));
        docBuilder.addParagraph(
                "Zrealizowane godziny: " + String.format("%.1f", totalMinutes / 60.0) + " h");
        docBuilder.addParagraph("\u0141\u0105cznie wydano (odbyte): " + totalSpent + " PLN");
        docBuilder.addParagraph(" ");

        PdfTableBuilder tableBuilder = new PdfTableBuilder(new float[] {2, 2, 3, 2, 2, 2});
        tableBuilder.addHeaders("Data", "Czas", "Korepetytor", "Przedmiot", "Status", "Kwota");

        for (Lesson l : lessons) {
            tableBuilder.addRowWithStatus(
                    l.getLessonStatus().name(),
                    l.getLessonDate().toString(),
                    l.getTimeFrom() + " - " + l.getTimeTo(),
                    l.getTutor().getUser().getFirstName()
                            + " "
                            + l.getTutor().getUser().getLastName(),
                    l.getSubject().getSubjectName(),
                    statusLabel(l.getLessonStatus()),
                    l.getAmount() + " PLN");
        }

        return docBuilder.addTable(tableBuilder.build()).build();
    }
}
