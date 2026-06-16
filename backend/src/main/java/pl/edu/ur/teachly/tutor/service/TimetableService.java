package pl.edu.ur.teachly.tutor.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.holiday.entity.Holiday;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.tutor.dto.response.TimeSlot;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityOverrideRepository;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityRecurringRepository;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;

/**
 * Serwis generujący plan dostępnych terminów korepetytora w zadanym przedziale dat.
 *
 * <p>Algorytm dla każdego dnia z zakresu:
 *
 * <ol>
 *   <li>Jeśli dzień jest świętem — brak terminów.
 *   <li>Jeśli istnieje jednorazowe nadpisanie ({@code override}) — użyj jego godzin (lub brak
 *       terminów, gdy brak godzin).
 *   <li>W przeciwnym razie użyj wpisów cyklicznych ({@code recurring}) pasujących do dnia tygodnia.
 *   <li>Odejmij terminy zajęte przez potwierdzone lekcje oraz lekcje oczekujące bieżącego ucznia.
 *   <li>Odfiltruj sloty z przeszłości oraz krótsze niż 30 minut.
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TutorAvailabilityRecurringRepository recurringRepository;
    private final TutorAvailabilityOverrideRepository overrideRepository;
    private final HolidayRepository holidayRepository;
    private final LessonRepository lessonRepository;
    private final TutorRepository tutorRepository;

    /**
     * Generuje plan wolnych terminów korepetytora dla podanego zakresu dat.
     *
     * <p>Jeśli korepetytor jest nieaktywny, zwracana jest pusta lista. Lekcje oczekujące bieżącego
     * ucznia są traktowane jako zajęte — dzięki temu uczeń widzi swoje własne rezerwacje jako
     * blokujące termin.
     *
     * @param tutorId identyfikator korepetytora
     * @param fromDate początek zakresu dat (włącznie)
     * @param toDate koniec zakresu dat (włącznie)
     * @param currentStudentId identyfikator zalogowanego ucznia ({@code null} jeśli nie dotyczy)
     * @return lista obiektów dziennych z dostępnymi slotami godzinowymi
     */
    public List<TimetableDayResponse> getTimetable(
            Integer tutorId, LocalDate fromDate, LocalDate toDate, Integer currentStudentId) {

        boolean tutorActive =
                tutorRepository
                        .findById(tutorId)
                        .map(
                                tutor ->
                                        tutor.getUser() != null
                                                && Boolean.TRUE.equals(
                                                        tutor.getUser().getIsActive()))
                        .orElse(false);
        if (!tutorActive) {
            return Collections.emptyList();
        }

        List<TutorAvailabilityRecurring> recurrings =
                recurringRepository.findByTutor_UserId(tutorId);
        List<TutorAvailabilityOverride> overrides =
                overrideRepository.findByTutor_UserIdAndOverrideDateBetween(
                        tutorId, fromDate, toDate);
        List<Holiday> holidays = holidayRepository.findByHolidayDateBetween(fromDate, toDate);
        List<Lesson> lessons =
                lessonRepository.findByTutor_UserIdAndLessonDateBetween(tutorId, fromDate, toDate);

        List<TimetableDayResponse> timetable = new ArrayList<>();

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            boolean isHoliday =
                    holidays.stream().anyMatch(h -> h.getHolidayDate().equals(currentDate));
            if (isHoliday) {
                timetable.add(new TimetableDayResponse(currentDate, Collections.emptyList()));
                continue;
            }

            Optional<TutorAvailabilityOverride> overrideOpt =
                    overrides.stream()
                            .filter(o -> o.getOverrideDate().equals(currentDate))
                            .findFirst();

            List<TimeSlot> freeBlocks = new ArrayList<>();

            if (overrideOpt.isPresent()) {
                TutorAvailabilityOverride override = overrideOpt.get();
                if (override.getTimeFrom() != null && override.getTimeTo() != null) {
                    freeBlocks.add(new TimeSlot(override.getTimeFrom(), override.getTimeTo()));
                }
            } else {
                int dayOfWeek = currentDate.getDayOfWeek().getValue();
                List<TutorAvailabilityRecurring> dayRecurrings =
                        recurrings.stream()
                                .filter(r -> r.getDayOfWeek() == dayOfWeek)
                                .filter(
                                        r ->
                                                r.getDateTo() == null
                                                        || !r.getDateTo().isBefore(currentDate))
                                .toList();

                for (TutorAvailabilityRecurring r : dayRecurrings) {
                    freeBlocks.add(new TimeSlot(r.getTimeFrom(), r.getTimeTo()));
                }
            }

            if (freeBlocks.isEmpty()) {
                timetable.add(new TimetableDayResponse(currentDate, Collections.emptyList()));
                continue;
            }

            List<Lesson> dayLessons =
                    lessons.stream()
                            .filter(
                                    l ->
                                            l.getLessonDate().equals(currentDate)
                                                    && (l.getLessonStatus()
                                                                    == LessonStatus.CONFIRMED
                                                            || (l.getLessonStatus()
                                                                            == LessonStatus.PENDING
                                                                    && l.getStudent()
                                                                            .getId()
                                                                            .equals(
                                                                                    currentStudentId))))
                            .toList();

            for (Lesson lesson : dayLessons) {
                freeBlocks = subtractLesson(freeBlocks, lesson.getTimeFrom(), lesson.getTimeTo());
            }

            final LocalTime minTime;
            if (currentDate.equals(LocalDate.now())) {
                LocalTime now = LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES);
                int remainder = now.getMinute() % 30;
                minTime = remainder == 0 ? now : now.plusMinutes(30 - remainder);
            } else {
                minTime = LocalTime.MIDNIGHT;
            }

            freeBlocks =
                    freeBlocks.stream()
                            .map(
                                    b ->
                                            b.getTimeFrom().isBefore(minTime)
                                                    ? new TimeSlot(minTime, b.getTimeTo())
                                                    : b)
                            .filter(b -> b.getTimeFrom().isBefore(b.getTimeTo()))
                            .filter(
                                    b ->
                                            java.time.Duration.between(
                                                                    b.getTimeFrom(), b.getTimeTo())
                                                            .toMinutes()
                                                    >= 30)
                            .sorted(Comparator.comparing(TimeSlot::getTimeFrom))
                            .collect(Collectors.toList());

            timetable.add(new TimetableDayResponse(currentDate, freeBlocks));
        }

        return timetable;
    }

    /**
     * Odejmuje zajęty przedział lekcji od listy wolnych bloków czasowych.
     *
     * <p>Blok, który nie pokrywa się z lekcją, jest przepuszczany bez zmian. Blok częściowo pokryty
     * jest dzielony — pozostają fragmenty przed i po lekcji.
     *
     * @param blocks lista wolnych slotów do przycięcia
     * @param lessonStart godzina rozpoczęcia lekcji
     * @param lessonEnd godzina zakończenia lekcji
     * @return zaktualizowana lista wolnych slotów po odjęciu lekcji
     */
    private List<TimeSlot> subtractLesson(
            List<TimeSlot> blocks, LocalTime lessonStart, LocalTime lessonEnd) {
        List<TimeSlot> updatedBlocks = new ArrayList<>();
        for (TimeSlot block : blocks) {

            if (!lessonStart.isBefore(block.getTimeTo())
                    || !lessonEnd.isAfter(block.getTimeFrom())) {
                updatedBlocks.add(block);
                continue;
            }

            if (lessonStart.isAfter(block.getTimeFrom())) {
                updatedBlocks.add(new TimeSlot(block.getTimeFrom(), lessonStart));
            }
            if (lessonEnd.isBefore(block.getTimeTo())) {
                updatedBlocks.add(new TimeSlot(lessonEnd, block.getTimeTo()));
            }
        }
        return updatedBlocks;
    }
}
