package pl.edu.ur.teachly.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.edu.ur.teachly.common.enums.PaymentStatus;

public record PaymentStatusRequest(
        @NotNull(message = "Status płatności jest wymagany") PaymentStatus paymentStatus) {}
