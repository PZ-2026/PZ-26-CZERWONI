package pl.edu.ur.teachly.common.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.edu.ur.teachly.common.security.CustomAccessDeniedHandler;
import pl.edu.ur.teachly.common.security.CustomAuthenticationEntryPoint;
import pl.edu.ur.teachly.common.security.JwtAuthFilter;

/**
 * Główna konfiguracja Spring Security dla aplikacji Teachly.
 *
 * <p>Wyłącza sesje (stateless), konfiguruje CORS, rejestruje filtr JWT oraz definiuje reguły
 * autoryzacji dla publicznych i chronionych endpointów. Bezpieczeństwo na poziomie metod jest
 * włączone przez {@link EnableMethodSecurity}.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Lista dozwolonych źródeł CORS (rozdzielona przecinkami). Konfigurowalna przez właściwość
     * {@code application.cors.allowed-origins}; domyślnie tylko localhost (środowisko
     * deweloperskie).
     */
    @Value("${application.cors.allowed-origins:http://localhost,http://localhost:8080}")
    private String allowedOrigins;

    /**
     * Definiuje łańcuch filtrów bezpieczeństwa.
     *
     * <p>Ścieżki {@code /api/auth/**} i {@code /uploads/**} są publicznie dostępne. Pozostałe
     * żądania wymagają uwierzytelnienia przez token JWT.
     *
     * @param http konfiguracja HTTP Security
     * @return skonfigurowany {@link SecurityFilterChain}
     * @throws Exception w przypadku błędu konfiguracji
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/auth/**", "/uploads/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        exceptions ->
                                exceptions
                                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Konfiguruje politykę CORS ograniczoną do skonfigurowanej listy źródeł.
     *
     * <p>Uwierzytelnianie opiera się na tokenie JWT w nagłówku {@code Authorization} (a nie na
     * ciasteczkach), dlatego {@code allowCredentials} jest wyłączone — eliminuje to antywzorzec
     * „wildcard + poświadczenia".
     *
     * @return źródło konfiguracji CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
