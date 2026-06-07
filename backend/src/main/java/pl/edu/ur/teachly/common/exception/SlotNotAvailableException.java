package pl.edu.ur.teachly.common.exception;

/**
 * Wyjątek zgłaszany gdy wybrany termin lekcji jest niedostępny lub koliduje z inną rezerwacją.
 *
 * <p>Mapowany przez {@link GlobalExceptionHandler} na odpowiedź HTTP 409 Conflict.
 */
public class SlotNotAvailableException extends RuntimeException {

    /**
     * Tworzy wyjątek z podanym komunikatem opisującym konflikt terminów.
     *
     * @param message opis konfliktu terminu
     */
    public SlotNotAvailableException(String message) {
        super(message);
    }
}
