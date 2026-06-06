package pl.edu.ur.teachly.common.util;

import java.util.Locale;

public final class SearchQueryUtils {

    public static final char LIKE_ESCAPE = '\\';

    private SearchQueryUtils() {}

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static String toLikePattern(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return "%" + escapeLike(normalized.toLowerCase(Locale.ROOT)) + "%";
    }

    public static String normalizeLower(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

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
