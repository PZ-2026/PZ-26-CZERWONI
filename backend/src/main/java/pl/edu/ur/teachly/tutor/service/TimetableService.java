package pl.edu.ur.teachly.tutor.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TutorAvailabilityRecurringRepository recurringRepository;
    private final TutorAvailabilityOverrideRepository overrideRepository;
    private final HolidayRepository holidayRepository;
    private final LessonRepository lessonRepository;

    public List<TimetableDayResponse> getTimetable(
            Integer tutorId, LocalDate fromDate, LocalDate toDate, Integer currentStudentId) {

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

            freeBlocks =
                    freeBlocks.stream()
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
