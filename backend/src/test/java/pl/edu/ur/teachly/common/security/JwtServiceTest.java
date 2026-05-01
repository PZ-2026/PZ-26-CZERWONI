package pl.edu.ur.teachly.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtService – testy jednostkowe")
class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET_KEY =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // 512 bit key

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1h
    }

    @Test
    @DisplayName("generateToken – generuje poprawny token")
    void generateToken_generatesToken() {
        UserDetails userDetails = new User("jan@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("jan@example.com");
    }

    @Test
    @DisplayName("isTokenValid – zwraca true dla poprawnego tokena")
    void isTokenValid_returnsTrueForValidToken() {
        UserDetails userDetails = new User("jan@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid – zwraca false dla innego użytkownika")
    void isTokenValid_returnsFalseForDifferentUser() {
        UserDetails userDetails = new User("jan@example.com", "password", Collections.emptyList());
        UserDetails otherUser = new User("other@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertThat(isValid).isFalse();
    }
}
