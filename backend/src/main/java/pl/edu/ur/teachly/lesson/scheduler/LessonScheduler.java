package pl.edu.ur.teachly.lesson.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.lesson.entity.Lesson;
import pl.edu.ur.teachly.lesson.repository.LessonRepository;

/**
 * Zadanie cykliczne anulujące lekcje oczekujące, których czas rozpoczęcia już minął.
 *
 * <p>Uruchamiane co minutę. Lekcja w statusie {@code PENDING} powinna zostać potwierdzona przez
 * korepetytora przed jej planowym rozpoczęciem — jeśli tego nie zrobił, lekcja jest automatycznie
 * anulowana.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LessonScheduler {

    private final LessonRepository lessonRepository;

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredPendingLessons() {
        LocalDate today = LocalDate.now(ZONE);
        LocalTime nowTime = LocalTime.now(ZONE);

        List<Lesson> expired =
                lessonRepository.findExpiredByStatus(LessonStatus.PENDING, today, nowTime);

        if (!expired.isEmpty()) {
            expired.forEach(l -> l.setLessonStatus(LessonStatus.CANCELLED));
            lessonRepository.saveAll(expired);
            log.info("Anulowano {} przeterminowanych lekcji oczekujących", expired.size());
        }
    }
}
