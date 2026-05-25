package pl.edu.ur.teachly.report;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.report.library.ReportGenerator;
import pl.edu.ur.teachly.report.library.model.ReportData;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public byte[] generateReport(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            String type,
            List<String> includeFields) {
        // Safe check for includeFields
        List<String> fields =
                (includeFields == null)
                        ? Arrays.asList(
                                "price", "subject", "status", "tutor", "student", "charts", "notes",
                                "date", "time")
                        : includeFields.stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.toList());

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

        // Sort by date and starting time
        lessons.sort(
                Comparator.comparing(Lesson::getLessonDate).thenComparing(Lesson::getTimeFrom));

        try {
            ReportData reportData;
            if (user.getUserRole() == UserRole.ADMIN) {
                reportData =
                        switch (type.toUpperCase()) {
                            case "REVENUE" ->
                                    buildAdminRevenueReport(lessons, startDate, endDate, fields);
                            case "USERS" -> buildAdminUsersReport(startDate, endDate, fields);
                            default -> buildAdminLessonsReport(lessons, startDate, endDate, fields);
                        };
            } else if (user.getUserRole() == UserRole.TUTOR) {
                reportData =
                        switch (type.toUpperCase()) {
                            case "REVENUE" ->
                                    buildTutorRevenueReport(
                                            user, lessons, startDate, endDate, fields);
                            case "STUDENTS" ->
                                    buildTutorStudentsReport(
                                            user, lessons, startDate, endDate, fields);
                            default ->
                                    buildTutorLessonsReport(
                                            user, lessons, startDate, endDate, fields);
                        };
            } else { // STUDENT
                reportData =
                        switch (type.toUpperCase()) {
                            case "EXPENSES" ->
                                    buildStudentExpensesReport(
                                            user, lessons, startDate, endDate, fields);
                            case "ANALYTICS" ->
                                    buildStudentAnalyticsReport(
                                            user, lessons, startDate, endDate, fields);
                            default ->
                                    buildStudentLessonsReport(
                                            user, lessons, startDate, endDate, fields);
                        };
            }

            return ReportGenerator.generate(reportData);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Wystąpił błąd podczas kompilowania raportu: " + e.getMessage(), e);
        }
    }

    private String statusLabel(LessonStatus status) {
        return switch (status) {
            case COMPLETED -> "Odbyta";
            case CANCELLED -> "Anulowana";
            case CONFIRMED -> "Potwierdzona";
            case PENDING -> "Oczekująca";
        };
    }

    private String periodLabel(LocalDate startDate, LocalDate endDate) {
        if (startDate.equals(endDate)) {
            return "Dzień: " + startDate;
        }
        return "Okres: " + startDate + " do " + endDate;
    }

    // ==========================================
    // STUDENT REPORTS
    // ==========================================

    private ReportData buildStudentLessonsReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Ucznia - Historia Lekcji");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        // Summaries
        data.addSummary("Liczba wszystkich zaplanowanych lekcji w okresie: " + lessons.size());
        long completedCount =
                lessons.stream().filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED).count();
        data.addSummary("Lekcje zrealizowane: " + completedCount);

        // Table setup
        boolean hasAnyColumn =
                fields.contains("tutor")
                        || fields.contains("subject")
                        || fields.contains("status")
                        || fields.contains("price")
                        || fields.contains("date")
                        || fields.contains("time");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("date")) {
                headers.add("Data");
            }
            if (fields.contains("time")) {
                headers.add("Czas");
            }
            if (fields.contains("tutor")) {
                headers.add("Korepetytor");
            }
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Status");
            }
            if (fields.contains("price")) {
                headers.add("Cena");
            }

            data.setHeaders(headers);

            for (Lesson l : lessons) {
                List<String> row = new ArrayList<>();
                if (fields.contains("date")) {
                    row.add(l.getLessonDate().toString());
                }
                if (fields.contains("time")) {
                    row.add(l.getTimeFrom() + " - " + l.getTimeTo());
                }
                if (fields.contains("tutor")) {
                    row.add(
                            l.getTutor().getUser().getFirstName()
                                    + " "
                                    + l.getTutor().getUser().getLastName());
                }
                if (fields.contains("subject")) {
                    row.add(l.getSubject().getSubjectName());
                }
                if (fields.contains("status")) {
                    row.add(statusLabel(l.getLessonStatus()));
                }
                if (fields.contains("price")) {
                    row.add(l.getAmount() + " PLN");
                }
                data.addRow(row, l.getLessonStatus().name());
            }
        }

        return data;
    }

    private ReportData buildStudentExpensesReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Ucznia - Analiza Wydatków");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());

        BigDecimal totalSpent =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        data.addSummary(
                "Łączna kwota wydana na lekcje odbyte w wybranym okresie: " + totalSpent + " PLN");
        data.addSummary("Liczba lekcji uwzględnionych w finansach: " + completed.size());

        // Expense by subject mapping
        Map<String, Double> expensesBySubject = new HashMap<>();
        for (Lesson l : completed) {
            String subject = l.getSubject().getSubjectName();
            expensesBySubject.put(
                    subject,
                    expensesBySubject.getOrDefault(subject, 0.0) + l.getAmount().doubleValue());
        }

        // Add Bar Chart if requested
        if (fields.contains("charts") && !expensesBySubject.isEmpty()) {
            data.setChartTitle("Wydatki w podziale na przedmioty (PLN)");
            data.setChartType("BAR");
            for (Map.Entry<String, Double> entry : expensesBySubject.entrySet()) {
                data.addChartItem(entry.getKey(), entry.getValue());
            }
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("subject")
                        || fields.contains("tutor")
                        || fields.contains("price")
                        || fields.contains("date");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("date")) {
                headers.add("Data");
            }
            if (fields.contains("tutor")) {
                headers.add("Korepetytor");
            }
            if (fields.contains("price")) {
                headers.add("Kwota");
            }
            data.setHeaders(headers);

            for (Lesson l : completed) {
                List<String> row = new ArrayList<>();
                if (fields.contains("subject")) {
                    row.add(l.getSubject().getSubjectName());
                }
                if (fields.contains("date")) {
                    row.add(l.getLessonDate().toString());
                }
                if (fields.contains("tutor")) {
                    row.add(
                            l.getTutor().getUser().getFirstName()
                                    + " "
                                    + l.getTutor().getUser().getLastName());
                }
                if (fields.contains("price")) {
                    row.add(l.getAmount() + " PLN");
                }
                data.addRow(row, l.getLessonStatus().name());
            }
        }

        return data;
    }

    private ReportData buildStudentAnalyticsReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Ucznia - Analiza Czasu i Korepetytorów");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());

        long totalMinutes =
                completed.stream()
                        .mapToLong(
                                l -> Duration.between(l.getTimeFrom(), l.getTimeTo()).toMinutes())
                        .sum();

        data.addSummary(
                "Łączny czas spędzony na lekcjach: "
                        + String.format("%.1f", totalMinutes / 60.0)
                        + " h ("
                        + totalMinutes
                        + " min)");

        // Tutor popularity mapping (for chart)
        Map<String, Double> hoursByTutor = new HashMap<>();
        // Tutor + Subject mapping (for table)
        Map<String, Double> hoursByTutorAndSubject = new java.util.LinkedHashMap<>();

        for (Lesson l : completed) {
            String tutorName =
                    l.getTutor().getUser().getFirstName()
                            + " "
                            + l.getTutor().getUser().getLastName();
            double hours = Duration.between(l.getTimeFrom(), l.getTimeTo()).toMinutes() / 60.0;
            hoursByTutor.put(tutorName, hoursByTutor.getOrDefault(tutorName, 0.0) + hours);

            String subjectName = l.getSubject().getSubjectName();
            String compositeKey = tutorName + " - " + subjectName;
            hoursByTutorAndSubject.put(
                    compositeKey, hoursByTutorAndSubject.getOrDefault(compositeKey, 0.0) + hours);
        }

        // Add Pie Chart
        if (fields.contains("charts") && !hoursByTutor.isEmpty()) {
            data.setChartTitle("Rozkład czasu nauki według korepetytorów (godziny)");
            data.setChartType("PIE");
            for (Map.Entry<String, Double> entry : hoursByTutor.entrySet()) {
                data.addChartItem(entry.getKey(), entry.getValue());
            }
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("tutor") || fields.contains("subject") || fields.contains("status");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("tutor")) {
                headers.add("Korepetytor");
            }
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Suma godzin nauki");
            }
            data.setHeaders(headers);

            for (Map.Entry<String, Double> entry : hoursByTutorAndSubject.entrySet()) {
                String[] parts = entry.getKey().split(" - ", 2);
                String tutor = parts[0];
                String subject = parts.length > 1 ? parts[1] : "";

                List<String> row = new ArrayList<>();
                if (fields.contains("tutor")) {
                    row.add(tutor);
                }
                if (fields.contains("subject")) {
                    row.add(subject);
                }
                if (fields.contains("status")) {
                    row.add(String.format("%.2f h", entry.getValue()));
                }
                data.addRow(row, "COMPLETED");
            }
        }

        return data;
    }

    // ==========================================
    // TUTOR REPORTS
    // ==========================================

    private ReportData buildTutorLessonsReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Korepetytora - Historia Zajęć");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        data.addSummary("Wszystkich zarejestrowanych lekcji: " + lessons.size());

        boolean hasAnyColumn =
                fields.contains("student")
                        || fields.contains("subject")
                        || fields.contains("status")
                        || fields.contains("price")
                        || fields.contains("date")
                        || fields.contains("time");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("date")) {
                headers.add("Data");
            }
            if (fields.contains("time")) {
                headers.add("Czas");
            }
            if (fields.contains("student")) {
                headers.add("Uczeń");
            }
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Status");
            }
            if (fields.contains("price")) {
                headers.add("Zarobek");
            }
            data.setHeaders(headers);

            for (Lesson l : lessons) {
                List<String> row = new ArrayList<>();
                if (fields.contains("date")) {
                    row.add(l.getLessonDate().toString());
                }
                if (fields.contains("time")) {
                    row.add(l.getTimeFrom() + " - " + l.getTimeTo());
                }
                if (fields.contains("student")) {
                    row.add(l.getStudent().getFirstName() + " " + l.getStudent().getLastName());
                }
                if (fields.contains("subject")) {
                    row.add(l.getSubject().getSubjectName());
                }
                if (fields.contains("status")) {
                    row.add(statusLabel(l.getLessonStatus()));
                }
                if (fields.contains("price")) {
                    row.add(l.getAmount() + " PLN");
                }
                data.addRow(row, l.getLessonStatus().name());
            }
        }

        return data;
    }

    private ReportData buildTutorRevenueReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Korepetytora - Podsumowanie Przychodów");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());

        BigDecimal totalRevenue =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        data.addSummary("Łączny przychód netto (lekcje zrealizowane): " + totalRevenue + " PLN");

        // Revenue by subject mapping
        Map<String, Double> revenueBySubject = new HashMap<>();
        for (Lesson l : completed) {
            String subject = l.getSubject().getSubjectName();
            revenueBySubject.put(
                    subject,
                    revenueBySubject.getOrDefault(subject, 0.0) + l.getAmount().doubleValue());
        }

        // Add Bar Chart
        if (fields.contains("charts") && !revenueBySubject.isEmpty()) {
            data.setChartTitle("Przychody w podziale na przedmioty (PLN)");
            data.setChartType("BAR");
            for (Map.Entry<String, Double> entry : revenueBySubject.entrySet()) {
                data.addChartItem(entry.getKey(), entry.getValue());
            }
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("subject") || fields.contains("status") || fields.contains("price");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Liczba lekcji");
            }
            if (fields.contains("price")) {
                headers.add("Łączny zarobek");
            }
            data.setHeaders(headers);

            for (Map.Entry<String, Double> entry : revenueBySubject.entrySet()) {
                long count =
                        completed.stream()
                                .filter(l -> l.getSubject().getSubjectName().equals(entry.getKey()))
                                .count();
                List<String> row = new ArrayList<>();
                if (fields.contains("subject")) {
                    row.add(entry.getKey());
                }
                if (fields.contains("status")) {
                    row.add(String.valueOf(count));
                }
                if (fields.contains("price")) {
                    row.add(String.format("%.2f PLN", entry.getValue()));
                }
                data.addRow(row, "COMPLETED");
            }
        }

        return data;
    }

    private ReportData buildTutorStudentsReport(
            User user,
            List<Lesson> lessons,
            LocalDate startDate,
            LocalDate endDate,
            List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Korepetytora - Analiza Uczniów");
        data.setSubtitle(
                user.getFirstName()
                        + " "
                        + user.getLastName()
                        + " | "
                        + periodLabel(startDate, endDate));

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());

        // Students lesson count mapping (for chart)
        Map<String, Double> lessonsByStudent = new HashMap<>();
        // Student + Subject mapping (for table)
        Map<String, Double> lessonsByStudentAndSubject = new java.util.LinkedHashMap<>();

        for (Lesson l : completed) {
            String studentName = l.getStudent().getFirstName() + " " + l.getStudent().getLastName();
            lessonsByStudent.put(
                    studentName, lessonsByStudent.getOrDefault(studentName, 0.0) + 1.0);

            String subjectName = l.getSubject().getSubjectName();
            String compositeKey = studentName + " - " + subjectName;
            lessonsByStudentAndSubject.put(
                    compositeKey, lessonsByStudentAndSubject.getOrDefault(compositeKey, 0.0) + 1.0);
        }

        data.addSummary("Liczba aktywnych uczniów w okresie: " + lessonsByStudent.size());
        data.addSummary("Łączna liczba przeprowadzonych lekcji: " + completed.size());

        // Add Pie Chart
        if (fields.contains("charts") && !lessonsByStudent.isEmpty()) {
            data.setChartTitle("Rozkład zajęć w podziale na uczniów (liczba lekcji)");
            data.setChartType("PIE");
            for (Map.Entry<String, Double> entry : lessonsByStudent.entrySet()) {
                data.addChartItem(entry.getKey(), entry.getValue());
            }
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("student")
                        || fields.contains("subject")
                        || fields.contains("status");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("student")) {
                headers.add("Imię i Nazwisko Ucznia");
            }
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Przeprowadzone Lekcje");
            }
            data.setHeaders(headers);

            for (Map.Entry<String, Double> entry : lessonsByStudentAndSubject.entrySet()) {
                String[] parts = entry.getKey().split(" - ", 2);
                String student = parts[0];
                String subject = parts.length > 1 ? parts[1] : "";

                List<String> row = new ArrayList<>();
                if (fields.contains("student")) {
                    row.add(student);
                }
                if (fields.contains("subject")) {
                    row.add(subject);
                }
                if (fields.contains("status")) {
                    row.add(String.format("%.0f", entry.getValue()));
                }
                data.addRow(row, "COMPLETED");
            }
        }

        return data;
    }

    // ==========================================
    // ADMIN REPORTS
    // ==========================================

    private ReportData buildAdminLessonsReport(
            List<Lesson> lessons, LocalDate startDate, LocalDate endDate, List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Administratora - Wszystkie Lekcje Platformy");
        data.setSubtitle(periodLabel(startDate, endDate));

        data.addSummary("Wszystkich zarejestrowanych lekcji w bazie: " + lessons.size());

        boolean hasAnyColumn =
                fields.contains("tutor")
                        || fields.contains("student")
                        || fields.contains("subject")
                        || fields.contains("status")
                        || fields.contains("price")
                        || fields.contains("date")
                        || fields.contains("time");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("date")) {
                headers.add("Data");
            }
            if (fields.contains("time")) {
                headers.add("Czas");
            }
            if (fields.contains("tutor")) {
                headers.add("Korepetytor");
            }
            if (fields.contains("student")) {
                headers.add("Uczeń");
            }
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Status");
            }
            if (fields.contains("price")) {
                headers.add("Kwota");
            }
            data.setHeaders(headers);

            for (Lesson l : lessons) {
                List<String> row = new ArrayList<>();
                if (fields.contains("date")) {
                    row.add(l.getLessonDate().toString());
                }
                if (fields.contains("time")) {
                    row.add(l.getTimeFrom() + " - " + l.getTimeTo());
                }
                if (fields.contains("tutor")) {
                    row.add(
                            l.getTutor().getUser().getFirstName()
                                    + " "
                                    + l.getTutor().getUser().getLastName());
                }
                if (fields.contains("student")) {
                    row.add(l.getStudent().getFirstName() + " " + l.getStudent().getLastName());
                }
                if (fields.contains("subject")) {
                    row.add(l.getSubject().getSubjectName());
                }
                if (fields.contains("status")) {
                    row.add(statusLabel(l.getLessonStatus()));
                }
                if (fields.contains("price")) {
                    row.add(l.getAmount() + " PLN");
                }
                data.addRow(row, l.getLessonStatus().name());
            }
        }

        return data;
    }

    private ReportData buildAdminRevenueReport(
            List<Lesson> lessons, LocalDate startDate, LocalDate endDate, List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Administratora - Obrót Platformy");
        data.setSubtitle(periodLabel(startDate, endDate));

        List<Lesson> completed =
                lessons.stream()
                        .filter(l -> l.getLessonStatus() == LessonStatus.COMPLETED)
                        .collect(Collectors.toList());

        BigDecimal totalRevenue =
                completed.stream().map(Lesson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        data.addSummary("Łączny obrót platformy (odbyte lekcje): " + totalRevenue + " PLN");
        data.addSummary("Liczba lekcji zrealizowanych: " + completed.size());

        // Group by subject
        Map<String, Double> revenueBySubject = new HashMap<>();
        for (Lesson l : completed) {
            String subject = l.getSubject().getSubjectName();
            revenueBySubject.put(
                    subject,
                    revenueBySubject.getOrDefault(subject, 0.0) + l.getAmount().doubleValue());
        }

        // Add Bar Chart
        if (fields.contains("charts") && !revenueBySubject.isEmpty()) {
            data.setChartTitle("Obrót platformy w podziale na przedmioty (PLN)");
            data.setChartType("BAR");
            for (Map.Entry<String, Double> entry : revenueBySubject.entrySet()) {
                data.addChartItem(entry.getKey(), entry.getValue());
            }
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("subject") || fields.contains("status") || fields.contains("price");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            if (fields.contains("subject")) {
                headers.add("Przedmiot");
            }
            if (fields.contains("status")) {
                headers.add("Liczba lekcji");
            }
            if (fields.contains("price")) {
                headers.add("Obrót");
            }
            data.setHeaders(headers);

            for (Map.Entry<String, Double> entry : revenueBySubject.entrySet()) {
                long count =
                        completed.stream()
                                .filter(l -> l.getSubject().getSubjectName().equals(entry.getKey()))
                                .count();
                List<String> row = new ArrayList<>();
                if (fields.contains("subject")) {
                    row.add(entry.getKey());
                }
                if (fields.contains("status")) {
                    row.add(String.valueOf(count));
                }
                if (fields.contains("price")) {
                    row.add(String.format("%.2f PLN", entry.getValue()));
                }
                data.addRow(row, "COMPLETED");
            }
        }

        return data;
    }

    private ReportData buildAdminUsersReport(
            LocalDate startDate, LocalDate endDate, List<String> fields) {
        ReportData data = new ReportData();
        data.setTitle("Raport Administratora - Analiza Użytkowników");
        data.setSubtitle(periodLabel(startDate, endDate));

        int totalStudents = userRepository.countByUserRole(UserRole.STUDENT);
        int totalTutors = userRepository.countByUserRole(UserRole.TUTOR);
        int totalAdmins = userRepository.countByUserRole(UserRole.ADMIN);
        int totalUsers = totalStudents + totalTutors + totalAdmins;

        data.addSummary("Łączna liczba zarejestrowanych kont na platformie: " + totalUsers);
        data.addSummary("Liczba Uczniów: " + totalStudents);
        data.addSummary("Liczba Korepetytorów: " + totalTutors);
        data.addSummary("Liczba Administratorów: " + totalAdmins);

        // Add Pie Chart of roles
        if (fields.contains("charts")) {
            data.setChartTitle("Rozkład ról zarejestrowanych użytkowników");
            data.setChartType("PIE");
            data.addChartItem("Uczniowie", totalStudents);
            data.addChartItem("Korepetytorzy", totalTutors);
            data.addChartItem("Administratorzy", totalAdmins);
        }

        // Table setup
        boolean hasAnyColumn =
                fields.contains("student") || fields.contains("tutor") || fields.contains("status");
        if (hasAnyColumn) {
            List<String> headers = new ArrayList<>();
            headers.add("Rola");
            headers.add("Liczba Kont");
            headers.add("Procentowy Udział");
            data.setHeaders(headers);

            double total = totalUsers > 0 ? totalUsers : 1.0;
            data.addRow(
                    Arrays.asList(
                            "STUDENT (Uczeń)",
                            String.valueOf(totalStudents),
                            String.format("%.1f%%", (totalStudents / total) * 100)),
                    "CONFIRMED");
            data.addRow(
                    Arrays.asList(
                            "TUTOR (Korepetytor)",
                            String.valueOf(totalTutors),
                            String.format("%.1f%%", (totalTutors / total) * 100)),
                    "COMPLETED");
            data.addRow(
                    Arrays.asList(
                            "ADMIN (Administrator)",
                            String.valueOf(totalAdmins),
                            String.format("%.1f%%", (totalAdmins / total) * 100)),
                    "PENDING");
        }

        return data;
    }
}
