package pl.edu.ur.teachly.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.ur.teachly.auth.dto.request.LoginRequest;
import pl.edu.ur.teachly.auth.dto.request.RegisterRequest;
import pl.edu.ur.teachly.auth.dto.response.AuthResponse;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.security.JwtService;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService – testy jednostkowe")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                UserRole.STUDENT,
                "Jan",
                "Kowalski",
                "jan@example.com",
                "123456789",
                "haslo123"
        );
    }

    // ─── register ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("register – sukces: zwraca AuthResponse z tokenem i rolą")
    void register_success() {
        User user = User.builder()
                .id(1)
                .email("jan@example.com")
                .phoneNumber("123456789")
                .userRole(UserRole.STUDENT)
                .build();

        when(userRepository.findByEmailOrPhoneNumber(any(), any())).thenReturn(Optional.empty());
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("haslo123")).thenReturn("hashed");
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo(UserRole.STUDENT);
        assertThat(response.userId()).isEqualTo(1);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("register – błąd: email już zajęty")
    void register_emailAlreadyTaken_throwsBusinessValidationException() {
        User existing = User.builder()
                .email("jan@example.com")
                .phoneNumber("999999999")
                .build();

        when(userRepository.findByEmailOrPhoneNumber("jan@example.com", "123456789"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Email");
    }

    @Test
    @DisplayName("register – błąd: numer telefonu już zajęty")
    void register_phoneAlreadyTaken_throwsBusinessValidationException() {
        User existing = User.builder()
                .email("other@example.com")
                .phoneNumber("123456789")
                .build();

        when(userRepository.findByEmailOrPhoneNumber("jan@example.com", "123456789"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("telefonu");
    }

    @Test
    @DisplayName("register – hasło jest hashowane przed zapisem")
    void register_passwordIsHashed() {
        User user = User.builder()
                .id(1)
                .email("jan@example.com")
                .phoneNumber("123456789")
                .userRole(UserRole.STUDENT)
                .build();

        when(userRepository.findByEmailOrPhoneNumber(any(), any())).thenReturn(Optional.empty());
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("haslo123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("token");

        authService.register(registerRequest);

        assertThat(user.getPasswordHash()).isEqualTo("bcrypt-hash");
    }

    // ─── login ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login – sukces: zwraca AuthResponse z tokenem")
    void login_success() {
        LoginRequest loginRequest = new LoginRequest("jan@example.com", "haslo123");
        User user = User.builder()
                .id(1)
                .email("jan@example.com")
                .userRole(UserRole.STUDENT)
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo(UserRole.STUDENT);
    }
}
