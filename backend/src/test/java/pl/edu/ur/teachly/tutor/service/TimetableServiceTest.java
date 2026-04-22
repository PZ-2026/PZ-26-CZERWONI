package pl.edu.ur.teachly.tutor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.holiday.entity.Holiday;
import pl.edu.ur.teachly.holiday.repository.HolidayRepository;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;
import pl.edu.ur.teachly.tutor.dto.response.TimeSlot;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityOverride;
import pl.edu.ur.teachly.tutor.entity.TutorAvailabilityRecurring;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityOverrideRepository;
import pl.edu.ur.teachly.tutor.repository.TutorAvailabilityRecurringRepository;
import pl.edu.ur.teachly.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableService – testy jednostkowe")
class TimetableServiceTest {

    @Mock private TutorAvailabilityRecurringRepository recurringRepository;
    @Mock private TutorAvailabilityOverrideRepository overrideRepository;
    @Mock private HolidayRepository holidayRepository;
    @Mock private LessonRepository lessonRepository;

    @InjectMocks
    private TimetableService timetableService;

    // ─── helpers ─────────────────────────────────────────────────────────────

    private static final Integer TUTOR_ID = 1;
    private static final Integer STUDENT_ID = 10;

    /** Poniedziałek = 1 w ISO (LocalDate.getDayOfWeek().getValue()) */
    private static final LocalDate MONDAY = LocalDate.of(2025, 6, 9);

    private TutorAvailabilityRecurring recurring(int dayOfWeek, LocalTime from, LocalTime to) {
        return TutorAvailabilityRecurring.builder()
                .tutor(mock(Tutor.class))
                .dayOfWeek((short) dayOfWeek)
                .timeFrom(from)
                .timeTo(to)
                .dateTo(null)
                .build();
    }

    private Lesson lessonOnDay(LocalDate date, LocalTime from, LocalTime to, LessonStatus status, Integer studentId) {
        User student = User.builder().id(studentId).build();
        Lesson l = new Lesson();
        l.setLessonDate(date);
        l.setTimeFrom(from);
        l.setTimeTo(to);
        l.setLessonStatus(status);
        l.setStudent(student);
        return l;
    }

    // ─── testy ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("dni wolne (holidays)")
    class HolidayTests {

        @Test
        @DisplayName("dzień wolny → pusta lista slotów")
        void holiday_returnsEmptySlots() {
            Holiday holiday = new Holiday();
            holiday.setHolidayDate(MONDAY);

            when(recurringRepository.findByTutor_UserId(TUTOR_ID)).thenReturn(List.of());
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of(holiday));
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAvailableSlots()).isEmpty();
        }
    }

    @Nested
    @DisplayName("dostępność cykliczna (recurring)")
    class RecurringTests {

        @Test
        @DisplayName("cykliczna dostępność poniedziałek 8-18, brak lekcji → jeden pełny slot")
        void recurring_noLessons_returnsFullSlot() {
            // dayOfWeek=1 → poniedziałek (ISO)
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAvailableSlots())
                    .containsExactly(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        }

        @Test
        @DisplayName("brak reguły dla danego dnia tygodnia → pusta lista slotów")
        void recurring_wrongDayOfWeek_returnsEmpty() {
            // dayOfWeek=2 = wtorek, a MONDAY = 1
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(2, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result.get(0).getAvailableSlots()).isEmpty();
        }

        @Test
        @DisplayName("lekcja CONFIRMED wycina środkowy czas – zwraca 2 sloty")
        void recurring_withConfirmedLesson_splitSlot() {
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());

            Lesson confirmed = lessonOnDay(MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), LessonStatus.CONFIRMED, 99);
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any()))
                    .thenReturn(List.of(confirmed));

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            List<TimeSlot> slots = result.get(0).getAvailableSlots();
            assertThat(slots).hasSize(2);
            assertThat(slots.get(0)).isEqualTo(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(10, 0)));
            assertThat(slots.get(1)).isEqualTo(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(18, 0)));
        }

        @Test
        @DisplayName("lekcja PENDING własnego studenta jest widoczna (wycina slot)")
        void recurring_ownPendingLesson_isSubtracted() {
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());

            // student_id = STUDENT_ID, status PENDING
            Lesson ownPending = lessonOnDay(MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0), LessonStatus.PENDING, STUDENT_ID);
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any()))
                    .thenReturn(List.of(ownPending));

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            List<TimeSlot> slots = result.get(0).getAvailableSlots();
            // Slot 8-12 i 13-18 (własna PENDING lekcja usunięta)
            assertThat(slots).hasSize(2);
        }

        @Test
        @DisplayName("lekcja PENDING innego studenta NIE wycina slotu")
        void recurring_otherStudentPendingLesson_isNotSubtracted() {
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());

            // Inny student (id=999), status PENDING
            Lesson otherPending = lessonOnDay(MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0), LessonStatus.PENDING, 999);
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any()))
                    .thenReturn(List.of(otherPending));

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            // Pełny slot, bo inna PENDING nie blokuje
            assertThat(result.get(0).getAvailableSlots())
                    .containsExactly(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        }

        @Test
        @DisplayName("slot krótszy niż 30 min po odjęciu lekcji jest odfiltrowany")
        void recurring_slotShorterThan30min_isFiltered() {
            // Slot 10:00–10:20 – za krótki po odjęciu lekcji
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(10, 0), LocalTime.of(10, 20))));
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result.get(0).getAvailableSlots()).isEmpty();
        }
    }

    @Nested
    @DisplayName("override dostępności")
    class OverrideTests {

        @Test
        @DisplayName("override z godzinami zastępuje regułę cykliczną")
        void override_withHours_replacesRecurring() {
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            TutorAvailabilityOverride override = TutorAvailabilityOverride.builder()
                    .overrideDate(MONDAY)
                    .timeFrom(LocalTime.of(14, 0))
                    .timeTo(LocalTime.of(16, 0))
                    .build();
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any()))
                    .thenReturn(List.of(override));

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result.get(0).getAvailableSlots())
                    .containsExactly(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(16, 0)));
        }

        @Test
        @DisplayName("override bez godzin (NULL) → cały dzień zablokowany")
        void override_nullHours_blocksWholeDay() {
            when(recurringRepository.findByTutor_UserId(TUTOR_ID))
                    .thenReturn(List.of(recurring(1, LocalTime.of(8, 0), LocalTime.of(18, 0))));
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            TutorAvailabilityOverride override = TutorAvailabilityOverride.builder()
                    .overrideDate(MONDAY)
                    .timeFrom(null)
                    .timeTo(null)
                    .build();
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any()))
                    .thenReturn(List.of(override));

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, MONDAY, STUDENT_ID);

            assertThat(result.get(0).getAvailableSlots()).isEmpty();
        }
    }

    @Nested
    @DisplayName("zakres wielu dni")
    class MultiDayTests {

        @Test
        @DisplayName("zakres poniedziałek–środa: zwraca 3 wpisy")
        void multiDay_returnsEntryPerDay() {
            LocalDate tuesday = MONDAY.plusDays(1);
            LocalDate wednesday = MONDAY.plusDays(2);

            when(recurringRepository.findByTutor_UserId(TUTOR_ID)).thenReturn(List.of());
            when(overrideRepository.findByTutor_UserIdAndOverrideDateBetween(any(), any(), any())).thenReturn(List.of());
            when(holidayRepository.findByHolidayDateBetween(any(), any())).thenReturn(List.of());
            when(lessonRepository.findByTutor_UserIdAndLessonDateBetween(any(), any(), any())).thenReturn(List.of());

            List<TimetableDayResponse> result = timetableService.getTimetable(TUTOR_ID, MONDAY, wednesday, STUDENT_ID);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getDate()).isEqualTo(MONDAY);
            assertThat(result.get(1).getDate()).isEqualTo(tuesday);
            assertThat(result.get(2).getDate()).isEqualTo(wednesday);
        }
    }
}
