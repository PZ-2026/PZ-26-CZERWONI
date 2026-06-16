package pl.edu.ur.teachly.common.exception;

/**
 * Wyjątek zgłaszany gdy żądany zasób nie istnieje w bazie danych.
 *
 * <p>Mapowany przez {@link GlobalExceptionHandler} na odpowiedź HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Tworzy wyjątek z podanym komunikatem identyfikującym brakujący zasób.
     *
     * @param message opis brakującego zasobu
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
