package pl.edu.ur.teachly.auth.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.auth.dto.request.LoginRequest;
import pl.edu.ur.teachly.auth.dto.request.RegisterRequest;
import pl.edu.ur.teachly.auth.dto.response.AuthResponse;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.security.JwtService;
import pl.edu.ur.teachly.tutor.entity.Tutor;
import pl.edu.ur.teachly.tutor.repository.TutorRepository;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

/**
 * Serwis obsługujący rejestrację i logowanie użytkowników.
 *
 * <p>Po pomyślnej rejestracji lub uwierzytelnieniu zwraca token JWT oraz podstawowe dane
 * użytkownika potrzebne aplikacji klienckiej.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TutorRepository tutorRepository;

    /**
     * Rejestruje nowego użytkownika w systemie.
     *
     * <p>Rejestracja jako administrator jest zablokowana. Jeśli podany adres e-mail lub numer
     * telefonu jest już zajęty, zgłaszany jest wyjątek walidacji. Dla roli {@link UserRole#TUTOR}
     * automatycznie tworzony jest profil korepetytora.
     *
     * @param request dane rejestracji
     * @return token JWT oraz rola i identyfikator nowego użytkownika
     * @throws BusinessValidationException gdy e-mail lub telefon są już zajęte, albo gdy próbuje
     *     się zarejestrować jako ADMIN
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (request.userRole() == UserRole.ADMIN) {
            throw new BusinessValidationException("Nie można zarejestrować się jako administrator");
        }

        userRepository
                .findByEmailOrPhoneNumber(request.email(), request.phoneNumber())
                .ifPresent(
                        user -> {
                            if (user.getEmail().equals(request.email())) {
                                throw new BusinessValidationException("Email jest już zajęty");
                            }
                            if (user.getPhoneNumber().equals(request.phoneNumber())) {
                                throw new BusinessValidationException(
                                        "Numer telefonu jest już zajęty");
                            }
                        });

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        if (user.getUserRole() == UserRole.TUTOR) {
            Tutor tutor =
                    Tutor.builder()
                            .user(user)
                            .hourlyRate(BigDecimal.ZERO)
                            .offersOnline(false)
                            .offersInPerson(false)
                            .build();
            tutorRepository.save(tutor);
        }

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken, user.getUserRole(), user.getId());
    }

    /**
     * Uwierzytelnia użytkownika na podstawie adresu e-mail i hasła.
     *
     * @param request dane logowania
     * @return token JWT oraz rola i identyfikator zalogowanego użytkownika
     * @throws org.springframework.security.core.AuthenticationException gdy dane są nieprawidłowe
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(), request.password()));

        User user = (User) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken, user.getUserRole(), user.getId());
    }
}
