package pl.edu.ur.teachly.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.ur.teachly.user.repository.UserRepository;

/**
 * Konfiguracja komponentów uwierzytelniania Spring Security.
 *
 * <p>Rejestruje beany: {@link UserDetailsService} (ładowanie użytkownika po e-mailu), {@link
 * AuthenticationProvider} (weryfikacja hasła BCrypt), {@link AuthenticationManager} oraz {@link
 * PasswordEncoder}.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Tworzy {@link UserDetailsService} ładujący użytkownika z bazy danych po adresie e-mail.
     *
     * @return implementacja UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
                userRepository
                        .findByEmail(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
    }

    /**
     * Tworzy {@link AuthenticationProvider} oparty na DAO z enkoderem BCrypt.
     *
     * @return skonfigurowany dostawca uwierzytelnienia
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Eksponuje {@link AuthenticationManager} jako bean Spring.
     *
     * @param config konfiguracja uwierzytelnienia
     * @return menedżer uwierzytelnienia
     * @throws Exception w przypadku błędu konfiguracji
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Tworzy enkoder haseł oparty na algorytmie BCrypt.
     *
     * @return enkoder BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
