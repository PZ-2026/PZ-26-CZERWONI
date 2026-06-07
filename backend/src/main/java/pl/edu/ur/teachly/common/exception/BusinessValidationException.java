package pl.edu.ur.teachly.common.exception;

/**
 * Wyjątek zgłaszany przy naruszeniu reguł biznesowych aplikacji.
 *
 * <p>Mapowany przez {@link GlobalExceptionHandler} na odpowiedź HTTP 400 Bad Request.
 */
public class BusinessValidationException extends RuntimeException {

    /**
     * Tworzy wyjątek z podanym komunikatem opisującym naruszoną regułę.
     *
     * @param message opis naruszenia reguły biznesowej
     */
    public BusinessValidationException(String message) {
        super(message);
    }
}
