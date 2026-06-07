package pl.edu.ur.teachly.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @Test
    @DisplayName("handleIllegalState – zwraca status 400")
    void handleIllegalState_returns400() {
        ProblemDetail detail = handler.handleIllegalState(new IllegalStateException("Bad state"));

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(detail.getDetail()).isEqualTo("Bad state");
    }

    @Test
    @DisplayName("handleAuthenticationException – zwraca status 401")
    void handleAuthenticationException_returns401() {
        ProblemDetail detail =
                handler.handleAuthenticationException(new BadCredentialsException("Bad creds"));

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(detail.getDetail()).isEqualTo("Nieprawidłowe dane użytkownika");
    }

    @Test
    @DisplayName("handleAccessDeniedException – zwraca status 403")
    void handleAccessDeniedException_returns403() {
        ProblemDetail detail =
                handler.handleAccessDeniedException(new AccessDeniedException("Denied"));

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(detail.getDetail()).isEqualTo("Brak wystarczających uprawnień");
    }

    @Test
    @DisplayName("handleValidationExceptions – zwraca 400 z mapą błędów")
    void handleValidationExceptions_returns400WithErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "req");
        bindingResult.addError(new FieldError("req", "email", "Nieprawidłowy email"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail detail = handler.handleValidationExceptions(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(detail.getProperties()).containsKey("validationErrors");
    }

    @Test
    @DisplayName("handleDataIntegrityViolation – rozpoznaje konflikt email")
    void handleDataIntegrityViolation_emailConflict() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("dup", new RuntimeException("email unique"));

        ProblemDetail detail = handler.handleDataIntegrityViolation(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(detail.getDetail()).isEqualTo("Email jest już zajęty");
    }

    @Test
    @DisplayName("handleDataIntegrityViolation – rozpoznaje konflikt telefonu")
    void handleDataIntegrityViolation_phoneConflict() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "dup", new RuntimeException("phone_number unique"));

        ProblemDetail detail = handler.handleDataIntegrityViolation(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(detail.getDetail()).isEqualTo("Numer telefonu jest już zajęty");
    }

    @Test
    @DisplayName("handleDataIntegrityViolation – domyślny komunikat")
    void handleDataIntegrityViolation_genericConflict() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "dup", new RuntimeException("other constraint"));

        ProblemDetail detail = handler.handleDataIntegrityViolation(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(detail.getDetail()).isEqualTo("Podane dane są już zajęte");
    }

    @Test
    @DisplayName("handleGlobalException – zwraca status 500")
    void handleGlobalException_returns500() {
        ProblemDetail detail = handler.handleGlobalException(new RuntimeException("Unexpected"));

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(detail.getDetail()).isEqualTo("Wystąpił nieoczekiwany błąd serwera");
    }
}
