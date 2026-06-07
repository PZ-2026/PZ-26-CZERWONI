package pl.edu.ur.teachly.common.enums;

/**
 * Status płatności za lekcję.
 *
 * <p>Zarządzany wyłącznie przez administratora — uczeń nie może samodzielnie zmieniać statusu
 * płatności.
 */
public enum PaymentStatus {
    /** Płatność oczekuje na potwierdzenie. */
    PENDING,

    /** Płatność zrealizowana. */
    PAID,

    /** Płatność anulowana (np. w wyniku anulowania lekcji). */
    CANCELLED
}
