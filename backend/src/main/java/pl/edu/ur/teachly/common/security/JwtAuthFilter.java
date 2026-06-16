package pl.edu.ur.teachly.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtr Spring Security wykonywany raz na żądanie HTTP.
 *
 * <p>Odczytuje nagłówek {@code Authorization: Bearer <token>}, weryfikuje token JWT i ustawia
 * uwierzytelnienie w {@link SecurityContextHolder}. Żądania do {@code /api/auth} są pomijane,
 * ponieważ nie wymagają tokenu.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Pomija filtr dla ścieżek uwierzytelniania, które nie wymagają tokenu JWT.
     *
     * @param request bieżące żądanie HTTP
     * @return {@code true} dla ścieżek zaczynających się od {@code /api/auth}
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth");
    }

    /**
     * Przetwarza żądanie HTTP: wyodrębnia i weryfikuje token JWT, a następnie ustawia kontekst
     * bezpieczeństwa dla uwierzytelnionego użytkownika.
     *
     * @param request żądanie HTTP
     * @param response odpowiedź HTTP
     * @param filterChain łańcuch filtrów
     * @throws ServletException w przypadku błędu serwletu
     * @throws IOException w przypadku błędu wejścia/wyjścia
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (JwtException ex) {
            log.warn(
                    "Invalid JWT token for request [{}]: {}",
                    request.getRequestURI(),
                    ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token is no longer valid for user [{}]", userEmail);
                }
            } catch (Exception ex) {
                log.error(
                        "Unexpected error during authentication for user [{}] on [{}]: {}",
                        userEmail,
                        request.getRequestURI(),
                        ex.getMessage(),
                        ex);
            }
        }
        filterChain.doFilter(request, response);
    }
}
