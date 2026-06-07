package pl.edu.ur.teachly.common.enums;

/**
 * Status lekcji odzwierciedlający jej aktualny etap w cyklu życia.
 *
 * <p>Dozwolone przejścia stanów:
 *
 * <pre>
 *   PENDING → CONFIRMED | CANCELLED
 *   CONFIRMED → COMPLETED | CANCELLED
 *   COMPLETED → (brak)
 *   CANCELLED → (brak)
 * </pre>
 */
public enum LessonStatus {
    /** Lekcja oczekuje na potwierdzenie przez korepetytora. */
    PENDING,

    /** Lekcja potwierdzona przez korepetytora, termin zarezerwowany. */
    CONFIRMED,

    /** Lekcja odbyła się i została oznaczona jako zakończona. */
    COMPLETED,

    /** Lekcja została anulowana przez jedną ze stron. */
    CANCELLED
}
