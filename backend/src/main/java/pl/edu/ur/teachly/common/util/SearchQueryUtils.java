package pl.edu.ur.teachly.common.util;

import java.util.Locale;

/**
 * Narzędzia pomocnicze do budowania wzorców wyszukiwania SQL LIKE.
 *
 * <p>Klasa jest nieinstancowalnym utility — wszystkie metody są statyczne. Zapewnia bezpieczne
 * escapowanie znaków specjalnych ({@code %}, {@code _}, {@code \}) przed użyciem wzorca w
 * zapytaniach JPQL/HQL.
 */
public final class SearchQueryUtils {

    /** Znak ucieczki stosowany przy escapowaniu wzorców LIKE. */
    public static final char LIKE_ESCAPE = '\\';

    private SearchQueryUtils() {}

    /**
     * Normalizuje wartość — przycina białe znaki i zwraca {@code null} dla pustych łańcuchów.
     *
     * @param value wartość do normalizacji
     * @return przycięta wartość lub {@code null}
     */
    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Konwertuje frazę wyszukiwania na wzorzec SQL LIKE ({@code %fraza%}).
     *
     * <p>Znaki specjalne są escapowane. Fraza jest zamieniana na małe litery przed opakowaniem w
     * wildcards, co umożliwia wyszukiwanie case-insensitive. Zwraca {@code null} jeśli wartość jest
     * pusta lub {@code null} (brak filtra).
     *
     * @param value fraza wyszukiwania
     * @return wzorzec LIKE gotowy do użycia w zapytaniu lub {@code null}
     */
    public static String toLikePattern(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return "%" + escapeLike(normalized.toLowerCase(Locale.ROOT)) + "%";
    }

    /**
     * Normalizuje wartość i zamienia ją na małe litery.
     *
     * @param value wartość do przetworzenia
     * @return znormalizowana wartość małymi literami lub {@code null}
     */
    public static String normalizeLower(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    /**
     * Escapuje znaki specjalne SQL LIKE ({@code %}, {@code _}, {@code \}) znakiem ucieczki.
     *
     * @param value wartość do escapowania
     * @return wartość z escapowanymi znakami specjalnymi
     */
    static String escapeLike(String value) {
        var escaped = new StringBuilder(value.length() + 8);
        for (char c : value.toCharArray()) {
            if (c == LIKE_ESCAPE || c == '%' || c == '_') {
                escaped.append(LIKE_ESCAPE);
            }
            escaped.append(c);
        }
        return escaped.toString();
    }
}
