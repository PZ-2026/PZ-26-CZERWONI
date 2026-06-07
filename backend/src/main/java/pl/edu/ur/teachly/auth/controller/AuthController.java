package pl.edu.ur.teachly.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.auth.dto.request.LoginRequest;
import pl.edu.ur.teachly.auth.dto.request.RegisterRequest;
import pl.edu.ur.teachly.auth.dto.response.AuthResponse;
import pl.edu.ur.teachly.auth.service.AuthService;

/**
 * Kontroler REST obsługujący endpointy uwierzytelniania.
 *
 * <p>Ścieżka bazowa: {@code /api/auth}. Endpointy tej klasy są publicznie dostępne i nie wymagają
 * tokenu JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Rejestruje nowego użytkownika i zwraca token JWT.
     *
     * @param request dane nowego użytkownika
     * @return token JWT, rola i identyfikator zarejestrowanego użytkownika
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Loguje użytkownika i zwraca token JWT.
     *
     * @param request dane logowania (e-mail i hasło)
     * @return token JWT, rola i identyfikator zalogowanego użytkownika
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
