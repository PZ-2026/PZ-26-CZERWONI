package pl.edu.ur.teachly.user.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.user.dto.request.AdminUserUpdateRequest;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.service.UserService;

/**
 * Kontroler REST obsługujący endpointy zarządzania użytkownikami.
 *
 * <p>Ścieżka bazowa: {@code /api/users}. Dostęp do poszczególnych operacji jest ograniczony rolami
 * — część endpointów jest dostępna wyłącznie dla administratora, część dla właściciela konta lub
 * administratora.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Zwraca listę użytkowników z opcjonalnym filtrowaniem. Dostępne tylko dla ADMIN.
     *
     * @param q fraza wyszukiwania (imię, nazwisko, e-mail)
     * @param role filtr roli
     * @param active filtr stanu aktywności
     * @return lista użytkowników spełniających kryteria
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active) {
        return userService.searchUsers(q, role, active);
    }

    /**
     * Zwraca dane użytkownika o podanym identyfikatorze. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param id identyfikator użytkownika
     * @return dane użytkownika
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    /**
     * Aktualizuje profil użytkownika. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param id identyfikator użytkownika
     * @param request nowe dane profilu
     * @return zaktualizowane dane użytkownika
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public UserResponse updateUserProfile(
            @PathVariable Integer id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUserProfile(id, request);
    }

    /**
     * Aktualizuje dane użytkownika przez administratora. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator użytkownika do edycji
     * @param request nowe dane użytkownika
     * @param currentUser aktualnie zalogowany administrator
     * @return zaktualizowane dane użytkownika
     */
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse adminUpdateUser(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUserUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        return userService.adminUpdateUser(id, request, currentUser);
    }

    /**
     * Aktywuje konto użytkownika. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator użytkownika
     * @param currentUser aktualnie zalogowany administrator
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        userService.activateUser(id, currentUser);
    }

    /**
     * Blokuje (dezaktywuje) konto użytkownika. Dostępne tylko dla ADMIN.
     *
     * @param id identyfikator użytkownika
     * @param currentUser aktualnie zalogowany administrator
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(
            @PathVariable Integer id, @AuthenticationPrincipal User currentUser) {
        userService.deactivateUser(id, currentUser);
    }

    /**
     * Przesyła awatar użytkownika. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param id identyfikator użytkownika
     * @param file przesłany plik obrazu (JPG lub PNG, max 5 MB)
     * @return zaktualizowane dane użytkownika z nowym URL awatara
     */
    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public UserResponse uploadAvatar(
            @PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(id, file);
    }

    /**
     * Usuwa awatar użytkownika. Dostępne dla ADMIN lub właściciela konta.
     *
     * @param id identyfikator użytkownika
     * @return zaktualizowane dane użytkownika bez awatara
     */
    @DeleteMapping("/{id}/avatar")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public UserResponse deleteAvatar(@PathVariable Integer id) {
        return userService.deleteAvatar(id);
    }
}
