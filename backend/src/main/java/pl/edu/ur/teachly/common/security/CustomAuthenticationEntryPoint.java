package pl.edu.ur.teachly.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Punkt wejścia obsługujący brak uwierzytelnienia (HTTP 401).
 *
 * <p>Przekazuje wyjątek {@link AuthenticationException} do Spring MVC {@link
 * HandlerExceptionResolver}, dzięki czemu odpowiedź jest generowana przez {@code
 * GlobalExceptionHandler} w spójnym formacie JSON (ProblemDetail).
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Obsługuje brak uwierzytelnienia, delegując wyjątek do resolvera wyjątków Spring MVC.
     *
     * @param request żądanie HTTP
     * @param response odpowiedź HTTP
     * @param authException wyjątek uwierzytelnienia
     */
    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException) {
        resolver.resolveException(request, response, null, authException);
    }
}
