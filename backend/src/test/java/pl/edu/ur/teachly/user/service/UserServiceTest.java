package pl.edu.ur.teachly.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.user.dto.request.AdminUserUpdateRequest;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService – testy jednostkowe")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    // ─── getUserById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById – sukces: zwraca UserResponse dla istniejącego użytkownika")
    void getUserById_found_returnsResponse() {
        User user = User.builder().id(1).email("a@b.com").userRole(UserRole.STUDENT).build();
        UserResponse expected =
                new UserResponse(
                        1,
                        "Jan",
                        "Kowalski",
                        "a@b.com",
                        "123456789",
                        null,
                        UserRole.STUDENT,
                        true,
                        null,
                        null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse result = userService.getUserById(1);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getUserById – błąd: użytkownik nie istnieje")
    void getUserById_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("użytkownika");
    }

    // ─── getAllUsers ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers – zwraca listę wszystkich użytkowników")
    void getAllUsers_returnsAllUsers() {
        User u1 = User.builder().id(1).build();
        User u2 = User.builder().id(2).build();
        UserResponse r1 =
                new UserResponse(
                        1,
                        "A",
                        "B",
                        "a@b.com",
                        "111111111",
                        null,
                        UserRole.STUDENT,
                        true,
                        null,
                        null);
        UserResponse r2 =
                new UserResponse(
                        2,
                        "C",
                        "D",
                        "c@d.com",
                        "222222222",
                        null,
                        UserRole.TUTOR,
                        true,
                        null,
                        null);

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toResponse(u1)).thenReturn(r1);
        when(userMapper.toResponse(u2)).thenReturn(r2);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).containsExactly(r1, r2);
    }

    @Test
    @DisplayName("getAllUsers – zwraca pustą listę gdy brak użytkowników")
    void getAllUsers_empty_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.getAllUsers()).isEmpty();
    }

    // ─── deactivateUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deactivateUser – sukces: ustawia isActive = false")
    void deactivateUser_success() {
        User user = User.builder().id(1).isActive(true).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deactivateUser(1);

        assertThat(user.getIsActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deactivateUser – błąd: użytkownik nie istnieje")
    void deactivateUser_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivateUser(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── activateUser ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("activateUser – sukces: ustawia isActive = true")
    void activateUser_success() {
        User user = User.builder().id(1).isActive(false).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.activateUser(1);

        assertThat(user.getIsActive()).isTrue();
        verify(userRepository).save(user);
    }

    // ─── updateUserProfile ────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUserProfile – sukces")
    void updateUserProfile_success() {
        User user = User.builder().id(1).build();
        UserUpdateRequest req = new UserUpdateRequest("Nowe", "Imie", null);
        UserResponse response =
                new UserResponse(
                        1,
                        "Nowe",
                        "Imie",
                        "a@b.com",
                        "123",
                        null,
                        UserRole.STUDENT,
                        true,
                        null,
                        null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUserProfile(1, req);

        assertThat(result).isEqualTo(response);
        verify(userMapper).updateFromRequest(req, user);
        verify(userRepository).save(user);
    }

    // ─── adminUpdateUser ──────────────────────────────────────────────────────

    @Test
    @DisplayName("adminUpdateUser – sukces")
    void adminUpdateUser_success() {
        User user = User.builder().id(1).build();
        AdminUserUpdateRequest req =
                new AdminUserUpdateRequest("A", "B", "a@b.pl", "123456789", UserRole.TUTOR, "url");
        UserResponse response =
                new UserResponse(
                        1,
                        "A",
                        "B",
                        "a@b.pl",
                        "123456789",
                        "url",
                        UserRole.TUTOR,
                        true,
                        null,
                        null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.adminUpdateUser(1, req);

        assertThat(result).isEqualTo(response);
        assertThat(user.getFirstName()).isEqualTo("A");
        assertThat(user.getLastName()).isEqualTo("B");
        assertThat(user.getEmail()).isEqualTo("a@b.pl");
        assertThat(user.getUserRole()).isEqualTo(UserRole.TUTOR);
        verify(userRepository).save(user);
    }
}
