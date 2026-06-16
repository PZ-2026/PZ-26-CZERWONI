package pl.edu.ur.teachly.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
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
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @AfterEach
    void cleanupAvatars() throws Exception {
        java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/avatars");
        if (java.nio.file.Files.exists(uploadPath)) {
            try (var walk = java.nio.file.Files.walk(uploadPath)) {
                walk.sorted(java.util.Comparator.reverseOrder())
                        .forEach(
                                path -> {
                                    try {
                                        java.nio.file.Files.deleteIfExists(path);
                                    } catch (java.io.IOException ignored) {
                                    }
                                });
            }
        }
    }

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

        when(userRepository.searchUsers(null, null, null)).thenReturn(List.of(u1, u2));
        when(userMapper.toResponse(u1)).thenReturn(r1);
        when(userMapper.toResponse(u2)).thenReturn(r2);

        List<UserResponse> result = userService.searchUsers(null, null, null);

        assertThat(result).containsExactly(r1, r2);
    }

    @Test
    @DisplayName("getAllUsers – zwraca pustą listę gdy brak użytkowników")
    void getAllUsers_empty_returnsEmptyList() {
        when(userRepository.searchUsers(null, null, null)).thenReturn(List.of());

        assertThat(userService.searchUsers(null, null, null)).isEmpty();
    }

    // ─── deactivateUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deactivateUser – sukces: ustawia isActive = false")
    void deactivateUser_success() {
        User admin = User.builder().id(2).isActive(true).build();
        User user = User.builder().id(1).isActive(true).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deactivateUser(1, admin);

        assertThat(user.getIsActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deactivateUser – błąd: admin próbuje zablokować własne konto")
    void deactivateUser_self_throwsBusinessValidationException() {
        User admin = User.builder().id(1).isActive(true).build();

        assertThatThrownBy(() -> userService.deactivateUser(1, admin))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Nie możesz zablokować własnego konta");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deactivateUser – błąd: użytkownik nie istnieje")
    void deactivateUser_notFound_throwsResourceNotFoundException() {
        User admin = User.builder().id(2).isActive(true).build();
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivateUser(99, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── activateUser ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("activateUser – sukces: ustawia isActive = true")
    void activateUser_success() {
        User user = User.builder().id(1).isActive(false).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User admin = User.builder().id(2).isActive(true).build();
        userService.activateUser(1, admin);

        assertThat(user.getIsActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("activateUser – błąd: użytkownik nie istnieje")
    void activateUser_notFound_throwsResourceNotFoundException() {
        User admin = User.builder().id(2).isActive(true).build();
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateUser(99, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── updateUserProfile ────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUserProfile – sukces")
    void updateUserProfile_success() {
        User user = User.builder().id(1).build();
        UserUpdateRequest req = new UserUpdateRequest("Nowe", "Imie", null, null, null, null);
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

    @Test
    @DisplayName("updateUserProfile – błąd: użytkownik nie istnieje")
    void updateUserProfile_notFound_throwsResourceNotFoundException() {
        UserUpdateRequest req =
                new UserUpdateRequest("Jan", "Kowalski", "a@b.com", "123456789", null, null);
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserProfile(99, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateUserProfile – błąd: hasło za krótkie")
    void updateUserProfile_shortPassword_throwsBusinessValidationException() {
        User user = User.builder().id(1).build();
        UserUpdateRequest req =
                new UserUpdateRequest("Jan", "Kowalski", "a@b.com", "123456789", "1234567", null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateUserProfile(1, req))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("8 znaków");
    }

    @Test
    @DisplayName("updateUserProfile – sukces: aktualizuje hasło")
    void updateUserProfile_password_success() {
        User user = User.builder().id(1).passwordHash("old").build();
        UserUpdateRequest req =
                new UserUpdateRequest(
                        "Jan", "Kowalski", "a@b.com", "123456789", "newpass123", null);
        UserResponse response =
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
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUserProfile(1, req);

        assertThat(result).isEqualTo(response);
        assertThat(user.getPasswordHash()).isEqualTo("encoded");
        verify(passwordEncoder).encode("newpass123");
    }

    @Test
    @DisplayName("updateUserProfile – puste hasło nie zmienia hasła")
    void updateUserProfile_blankPassword_doesNotChangePassword() {
        User user = User.builder().id(1).passwordHash("unchanged").build();
        UserUpdateRequest req =
                new UserUpdateRequest("Jan", "Kowalski", "a@b.com", "123456789", "   ", null);
        UserResponse response =
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
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.updateUserProfile(1, req);

        assertThat(user.getPasswordHash()).isEqualTo("unchanged");
        verify(passwordEncoder, never()).encode(any());
    }

    // ─── adminUpdateUser ──────────────────────────────────────────────────────

    @Test
    @DisplayName("adminUpdateUser – sukces")
    void adminUpdateUser_success() {
        User admin = User.builder().id(2).build();
        User user = User.builder().id(1).build();
        AdminUserUpdateRequest req =
                new AdminUserUpdateRequest("A", "B", "a@b.pl", "123456789", UserRole.TUTOR);
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

        UserResponse result = userService.adminUpdateUser(1, req, admin);

        assertThat(result).isEqualTo(response);
        assertThat(user.getFirstName()).isEqualTo("A");
        assertThat(user.getLastName()).isEqualTo("B");
        assertThat(user.getEmail()).isEqualTo("a@b.pl");
        assertThat(user.getUserRole()).isEqualTo(UserRole.TUTOR);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("adminUpdateUser – sukces: null rola nie nadpisuje istniejącej")
    void adminUpdateUser_nullRole_keepsExistingRole() {
        User admin = User.builder().id(2).build();
        User user = User.builder().id(1).userRole(UserRole.TUTOR).build();
        AdminUserUpdateRequest req =
                new AdminUserUpdateRequest("A", "B", "a@b.pl", "123456789", null);
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.pl", "123456789", null, UserRole.TUTOR, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.adminUpdateUser(1, req, admin);

        assertThat(user.getUserRole()).isEqualTo(UserRole.TUTOR);
    }

    @Test
    @DisplayName("adminUpdateUser – błąd: admin próbuje edytować własne konto")
    void adminUpdateUser_self_throwsBusinessValidationException() {
        User admin = User.builder().id(1).userRole(UserRole.ADMIN).build();
        AdminUserUpdateRequest req =
                new AdminUserUpdateRequest("A", "B", "a@b.pl", "123456789", UserRole.STUDENT);

        assertThatThrownBy(() -> userService.adminUpdateUser(1, req, admin))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Nie możesz edytować własnego konta z panelu administratora");
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("adminUpdateUser – błąd: użytkownik nie istnieje")
    void adminUpdateUser_notFound_throwsResourceNotFoundException() {
        User admin = User.builder().id(2).build();
        AdminUserUpdateRequest req =
                new AdminUserUpdateRequest("A", "B", "a@b.pl", "123456789", UserRole.TUTOR);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.adminUpdateUser(99, req, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── uploadAvatar ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("uploadAvatar – błąd: pusty plik")
    void uploadAvatar_emptyFile_throwsIllegalArgumentException() {
        MockMultipartFile emptyFile =
                new MockMultipartFile("file", "avatar.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> userService.uploadAvatar(1, emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Plik jest pusty");
    }

    @Test
    @DisplayName("uploadAvatar – błąd: plik za duży (ponad 5 MB)")
    void uploadAvatar_fileTooLarge_throwsIllegalArgumentException() {
        byte[] bigContent = new byte[6 * 1024 * 1024];
        MockMultipartFile bigFile =
                new MockMultipartFile("file", "big.jpg", "image/jpeg", bigContent);

        assertThatThrownBy(() -> userService.uploadAvatar(1, bigFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("za duży");
    }

    @Test
    @DisplayName("uploadAvatar – błąd: niedozwolony typ pliku")
    void uploadAvatar_invalidContentType_throwsIllegalArgumentException() {
        MockMultipartFile gifFile =
                new MockMultipartFile("file", "avatar.gif", "image/gif", new byte[100]);

        assertThatThrownBy(() -> userService.uploadAvatar(1, gifFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Niedozwolony format pliku");
    }

    @Test
    @DisplayName("uploadAvatar – błąd: poprawny Content-Type, ale zawartość nie jest obrazem")
    void uploadAvatar_fakeImageSignature_throwsIllegalArgumentException() {
        User user = User.builder().id(1).build();
        MockMultipartFile fakeImage =
                new MockMultipartFile(
                        "file", "avatar.jpg", "image/jpeg", "to nie jest obraz".getBytes());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.uploadAvatar(1, fakeImage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nie jest prawidłowym obrazem");
    }

    @Test
    @DisplayName("uploadAvatar – błąd: użytkownik nie istnieje")
    void uploadAvatar_userNotFound_throwsResourceNotFoundException() {
        MockMultipartFile file =
                new MockMultipartFile("file", "avatar.jpg", "image/jpeg", new byte[100]);
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.uploadAvatar(99, file))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("uploadAvatar – sukces: zapisuje plik JPEG i aktualizuje URL")
    void uploadAvatar_success_jpeg() throws Exception {
        User user = User.builder().id(1).avatarUrl(null).build();
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "avatar.jpg",
                        "image/jpeg",
                        new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0});
        UserResponse response =
                new UserResponse(
                        1,
                        "A",
                        "B",
                        "a@b.com",
                        "123",
                        "/uploads/avatars/x.jpg",
                        UserRole.STUDENT,
                        true,
                        null,
                        null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.uploadAvatar(1, file);

        assertThat(result).isEqualTo(response);
        assertThat(user.getAvatarUrl()).startsWith("/uploads/avatars/");
        assertThat(java.nio.file.Files.exists(java.nio.file.Paths.get("uploads/avatars"))).isTrue();
    }

    @Test
    @DisplayName("uploadAvatar – sukces: akceptuje image/png")
    void uploadAvatar_success_png() {
        User user = User.builder().id(1).build();
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "avatar.png",
                        "image/png",
                        new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47});
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.com", "123", null, UserRole.STUDENT, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.uploadAvatar(1, file);

        assertThat(user.getAvatarUrl()).startsWith("/uploads/avatars/");
    }

    @Test
    @DisplayName("uploadAvatar – sukces: usuwa poprzedni plik awatara")
    void uploadAvatar_replacesExistingAvatar() throws Exception {
        java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/avatars");
        java.nio.file.Files.createDirectories(uploadPath);
        java.nio.file.Path oldFile = uploadPath.resolve("old.jpg");
        java.nio.file.Files.write(oldFile, new byte[] {9});

        User user = User.builder().id(1).avatarUrl("/uploads/avatars/old.jpg").build();
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "new.jpg",
                        "image/jpg",
                        new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0});
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.com", "123", null, UserRole.STUDENT, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.uploadAvatar(1, file);

        assertThat(java.nio.file.Files.exists(oldFile)).isFalse();
        assertThat(user.getAvatarUrl()).isNotEqualTo("/uploads/avatars/old.jpg");
    }

    @Test
    @DisplayName("uploadAvatar – błąd: brak content type")
    void uploadAvatar_nullContentType_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", null, new byte[] {1});

        assertThatThrownBy(() -> userService.uploadAvatar(1, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Niedozwolony format pliku");
    }

    // ─── deleteAvatar ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAvatar – sukces: czyści avatarUrl gdy użytkownik nie ma awatara")
    void deleteAvatar_noExistingFile_clearsAvatarUrl() {
        User user = User.builder().id(1).avatarUrl(null).build();
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.com", "123", null, UserRole.STUDENT, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.deleteAvatar(1);

        assertThat(user.getAvatarUrl()).isNull();
        assertThat(result).isEqualTo(response);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deleteAvatar – sukces: usuwa plik z dysku")
    void deleteAvatar_removesExistingFile() throws Exception {
        java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/avatars");
        java.nio.file.Files.createDirectories(uploadPath);
        java.nio.file.Path avatarFile = uploadPath.resolve("existing.jpg");
        java.nio.file.Files.write(avatarFile, new byte[] {1, 2});

        User user = User.builder().id(1).avatarUrl("/uploads/avatars/existing.jpg").build();
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.com", "123", null, UserRole.STUDENT, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.deleteAvatar(1);

        assertThat(user.getAvatarUrl()).isNull();
        assertThat(java.nio.file.Files.exists(avatarFile)).isFalse();
    }

    @Test
    @DisplayName("deleteAvatar – ignoruje niebezpieczną ścieżkę awatara")
    void deleteAvatar_unsafePath_clearsUrlWithoutError() {
        User user = User.builder().id(1).avatarUrl("/uploads/avatars/../../secret.txt").build();
        UserResponse response =
                new UserResponse(
                        1, "A", "B", "a@b.com", "123", null, UserRole.STUDENT, true, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.deleteAvatar(1);

        assertThat(user.getAvatarUrl()).isNull();
    }

    @Test
    @DisplayName("deleteAvatar – błąd: użytkownik nie istnieje")
    void deleteAvatar_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteAvatar(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
