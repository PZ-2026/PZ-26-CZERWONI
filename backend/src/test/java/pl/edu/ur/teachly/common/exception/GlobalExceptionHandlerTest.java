package pl.edu.ur.teachly.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler – testy jednostkowe")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleResourceNotFound – zwraca status 404")
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ProblemDetail detail = handler.handleResourceNotFound(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(detail.getDetail()).isEqualTo("Not found");
    }

    @Test
    @DisplayName("handleBusinessValidation – zwraca status 400")
    void handleBusinessValidation_returns400() {
        BusinessValidationException ex = new BusinessValidationException("Invalid");
        ProblemDetail detail = handler.handleBusinessValidation(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(detail.getDetail()).isEqualTo("Invalid");
    }

    @Test
    @DisplayName("handleSlotNotAvailable – zwraca status 409")
    void handleSlotNotAvailable_returns409() {
        SlotNotAvailableException ex = new SlotNotAvailableException("Conflict");
        ProblemDetail detail = handler.handleSlotNotAvailable(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(detail.getDetail()).isEqualTo("Conflict");
    }

    @Test
    @DisplayName("handleIllegalArgument – zwraca status 400")
    void handleIllegalArgument_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal arg");
        ProblemDetail detail = handler.handleIllegalArgument(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(detail.getDetail()).isEqualTo("Illegal arg");
    }
}
