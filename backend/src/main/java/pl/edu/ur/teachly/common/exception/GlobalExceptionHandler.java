package pl.edu.ur.teachly.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Globalny handler wyjątków dla wszystkich kontrolerów REST.
 *
 * <p>Przechwytuje wyjątki aplikacyjne i mapuje je na ustandaryzowane odpowiedzi HTTP zgodne z
 * formatem {@link ProblemDetail} (RFC 9457). Dzięki temu klient zawsze otrzymuje spójną strukturę
 * błędu niezależnie od źródła wyjątku.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Obsługuje brak zasobu — zwraca HTTP 404.
     *
     * @param ex wyjątek z komunikatem identyfikującym brakujący zasób
     * @return szczegóły błędu 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Obsługuje naruszenie reguły biznesowej — zwraca HTTP 400.
     *
     * @param ex wyjątek z opisem naruszenia
     * @return szczegóły błędu 400
     */
    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidation(BusinessValidationException ex) {
        log.warn("Business validation failed: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Obsługuje konflikt terminów lekcji — zwraca HTTP 409.
     *
     * @param ex wyjątek z opisem konfliktu
     * @return szczegóły błędu 409
     */
    @ExceptionHandler(SlotNotAvailableException.class)
    public ProblemDetail handleSlotNotAvailable(SlotNotAvailableException ex) {
        log.warn("Slot conflict: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Obsługuje błędy walidacji Bean Validation — zwraca HTTP 400 z mapą błędów pól.
     *
     * @param ex wyjątek zawierający wyniki walidacji
     * @return szczegóły błędu 400 z właściwością {@code validationErrors}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Nieprawidłowe dane wejściowe");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        (error) -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(fieldName, errorMessage);
                        });

        log.warn("Validation failed: {}", errors);
        problemDetail.setProperty("validationErrors", errors);
        return problemDetail;
    }

    /**
     * Obsługuje błąd uwierzytelnienia — zwraca HTTP 401.
     *
     * @param ex wyjątek uwierzytelnienia
     * @return szczegóły błędu 401
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Nieprawidłowe dane użytkownika");
    }

    /**
     * Obsługuje brak uprawnień — zwraca HTTP 403.
     *
     * @param ex wyjątek odmowy dostępu
     * @return szczegóły błędu 403
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Brak wystarczających uprawnień");
    }

    /**
     * Obsługuje niedozwolony stan aplikacji — zwraca HTTP 400.
     *
     * @param ex wyjątek z opisem nieprawidłowego stanu
     * @return szczegóły błędu 400
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Obsługuje nieprawidłowy argument — zwraca HTTP 400.
     *
     * @param ex wyjątek z opisem błędnego argumentu
     * @return szczegóły błędu 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Obsługuje naruszenie integralności bazy danych — zwraca HTTP 409.
     *
     * <p>Próbuje rozpoznać przyczynę (duplikat e-mail lub numer telefonu) i zwrócić czytelny
     * komunikat w języku polskim.
     *
     * @param ex wyjątek naruszenia integralności
     * @return szczegóły błędu 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        String message = "Podane dane są już zajęte";
        String cause = ex.getMostSpecificCause().getMessage();
        if (cause != null) {
            if (cause.contains("email")) {
                message = "Email jest już zajęty";
            } else if (cause.contains("phone_number")) {
                message = "Numer telefonu jest już zajęty";
            }
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
    }

    /**
     * Obsługuje wszystkie pozostałe nieoczekiwane wyjątki — zwraca HTTP 500.
     *
     * @param ex nieoczekiwany wyjątek
     * @return szczegóły błędu 500
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        log.error("Unexpected server error: {}", ex.getMessage(), ex);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Wystąpił nieoczekiwany błąd serwera");
    }
}
