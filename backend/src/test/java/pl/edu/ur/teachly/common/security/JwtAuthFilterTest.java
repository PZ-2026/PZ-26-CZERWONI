package pl.edu.ur.teachly.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter – testy jednostkowe")
class JwtAuthFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthFilter jwtAuthFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("shouldNotFilter – pomija ścieżki /api/auth")
    void shouldNotFilter_authPath() {
        when(request.getServletPath()).thenReturn("/api/auth/login");

        assertThat(jwtAuthFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter – false dla chronionych ścieżek")
    void shouldNotFilter_protectedPath() {
        when(request.getServletPath()).thenReturn("/api/lessons");

        assertThat(jwtAuthFilter.shouldNotFilter(request)).isFalse();
    }

    @Test
    @DisplayName("doFilterInternal – brak nagłówka Authorization przepuszcza żądanie")
    void doFilterInternal_noAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal – niepoprawny token JWT przepuszcza żądanie")
    void doFilterInternal_invalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
        when(jwtService.extractUsername("bad-token")).thenThrow(new JwtException("invalid"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal – poprawny token ustawia kontekst bezpieczeństwa")
    void doFilterInternal_validToken() throws Exception {
        UserDetails userDetails =
                User.withUsername("jan@test.com").password("secret").roles("STUDENT").build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.extractUsername("valid-token")).thenReturn("jan@test.com");
        when(userDetailsService.loadUserByUsername("jan@test.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("jan@test.com");
    }

    @Test
    @DisplayName("doFilterInternal – wygasły token nie ustawia kontekstu")
    void doFilterInternal_expiredToken() throws Exception {
        UserDetails userDetails =
                User.withUsername("jan@test.com").password("secret").roles("STUDENT").build();

        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(jwtService.extractUsername("expired-token")).thenReturn("jan@test.com");
        when(userDetailsService.loadUserByUsername("jan@test.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("expired-token", userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("doFilterInternal – błąd ładowania użytkownika nie ustawia kontekstu")
    void doFilterInternal_userLoadFailure() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.extractUsername("valid-token")).thenReturn("jan@test.com");
        when(userDetailsService.loadUserByUsername("jan@test.com"))
                .thenThrow(
                        new org.springframework.security.core.userdetails.UsernameNotFoundException(
                                "missing"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
