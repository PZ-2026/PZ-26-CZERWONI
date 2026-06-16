package pl.edu.ur.teachly.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Handler obsługujący błąd HTTP 403 (odmowa dostępu).
 *
 * <p>Przekazuje wyjątek {@link AccessDeniedException} do Spring MVC {@link
 * HandlerExceptionResolver}, dzięki czemu odpowiedź jest generowana przez {@code
 * GlobalExceptionHandler} w spójnym formacie JSON (ProblemDetail).
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    public CustomAccessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Obsługuje odmowę dostępu, delegując wyjątek do resolvera wyjątków Spring MVC.
     *
     * @param request żądanie HTTP
     * @param response odpowiedź HTTP
     * @param accessDeniedException wyjątek odmowy dostępu
     */
    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException) {
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
