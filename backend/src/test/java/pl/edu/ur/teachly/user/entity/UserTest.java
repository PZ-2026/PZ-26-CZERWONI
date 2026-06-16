package pl.edu.ur.teachly.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.edu.ur.teachly.common.enums.UserRole;

@DisplayName("User – testy jednostkowe UserDetails")
class UserTest {

    @Test
    @DisplayName("UserDetails – zwraca dane konta i uprawnienia roli")
    void userDetails_returnsExpectedValues() {
        User user =
                User.builder()
                        .id(1)
                        .email("jan@test.com")
                        .passwordHash("hash")
                        .userRole(UserRole.TUTOR)
                        .isActive(true)
                        .build();

        assertThat(user.getUsername()).isEqualTo("jan@test.com");
        assertThat(user.getPassword()).isEqualTo("hash");
        assertThat(user.getAuthorities())
                .extracting(auth -> auth.getAuthority())
                .containsExactly("ROLE_TUTOR");
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("UserDetails – nieaktywne konto jest zablokowane i wyłączone")
    void userDetails_inactiveAccount() {
        User user =
                User.builder()
                        .email("jan@test.com")
                        .passwordHash("hash")
                        .userRole(UserRole.STUDENT)
                        .isActive(false)
                        .build();

        assertThat(user.isAccountNonLocked()).isFalse();
        assertThat(user.isEnabled()).isFalse();
    }
}
