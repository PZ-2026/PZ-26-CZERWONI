package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.edu.ur.teachly.common.enums.PaymentStatus;

/** Żądanie zmiany statusu płatności lekcji (dostępne wyłącznie dla administratora). */
public record PaymentStatusRequest(
        @NotNull(message = "Status płatności jest wymagany") PaymentStatus paymentStatus) {}
