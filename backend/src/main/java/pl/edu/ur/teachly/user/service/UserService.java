package pl.edu.ur.teachly.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.common.util.SearchQueryUtils;
import pl.edu.ur.teachly.user.dto.request.AdminUserUpdateRequest;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

/**
 * Serwis zarządzający kontami użytkowników.
 *
 * <p>Udostępnia operacje wyszukiwania, edycji profilu, zarządzania stanem konta (aktywacja/blokada)
 * oraz przesyłania i usuwania awatara.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Zwraca dane użytkownika o podanym identyfikatorze.
     *
     * @param id identyfikator użytkownika
     * @return dane użytkownika
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        return userRepository
                .findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Nie znaleziono szukanego użytkownika"));
    }

    /**
     * Wyszukuje użytkowników według frazy, roli i stanu aktywności.
     *
     * @param query fraza wyszukiwania (imię, nazwisko, e-mail)
     * @param role filtr roli użytkownika
     * @param active filtr stanu aktywności
     * @return lista pasujących użytkowników
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String query, UserRole role, Boolean active) {
        return userRepository
                .searchUsers(SearchQueryUtils.toLikePattern(query), role, active)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Aktualizuje profil użytkownika. Jeśli podano nowe hasło, jest ono hashowane przed zapisem.
     *
     * @param id identyfikator użytkownika
     * @param request nowe dane profilu
     * @return zaktualizowane dane użytkownika
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     * @throws BusinessValidationException gdy nowe hasło ma mniej niż 8 znaków
     */
    @Transactional
    public UserResponse updateUserProfile(Integer id, UserUpdateRequest request) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego użytkownika"));

        userMapper.updateFromRequest(request, user);

        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 8) {
                throw new BusinessValidationException("Hasło musi mieć co najmniej 8 znaków");
            }
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Aktualizuje dane użytkownika przez administratora.
     *
     * <p>Administrator nie może edytować własnego konta tą metodą.
     *
     * @param id identyfikator użytkownika do edycji
     * @param request nowe dane użytkownika
     * @param currentUser zalogowany administrator
     * @return zaktualizowane dane użytkownika
     * @throws BusinessValidationException gdy administrator próbuje edytować swoje konto
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional
    public UserResponse adminUpdateUser(
            Integer id, AdminUserUpdateRequest request, User currentUser) {
        if (currentUser.getId().equals(id)) {
            throw new BusinessValidationException(
                    "Nie możesz edytować własnego konta z panelu administratora");
        }
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego użytkownika"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        if (request.userRole() != null) {
            user.setUserRole(request.userRole());
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Aktywuje konto użytkownika.
     *
     * @param id identyfikator użytkownika
     * @param currentUser zalogowany administrator
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional
    public void activateUser(Integer id, User currentUser) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego użytkownika"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    /**
     * Blokuje konto użytkownika. Administrator nie może zablokować własnego konta.
     *
     * @param id identyfikator użytkownika
     * @param currentUser zalogowany administrator
     * @throws BusinessValidationException gdy administrator próbuje zablokować własne konto
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional
    public void deactivateUser(Integer id, User currentUser) {
        if (currentUser.getId().equals(id)) {
            throw new BusinessValidationException("Nie możesz zablokować własnego konta");
        }
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego użytkownika"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Przesyła i zapisuje awatar użytkownika. Poprzedni plik awatara jest usuwany.
     *
     * <p>Dopuszczalne formaty: JPG, PNG. Maksymalny rozmiar: 5 MB.
     *
     * @param id identyfikator użytkownika
     * @param file przesłany plik obrazu
     * @return zaktualizowane dane użytkownika z nowym URL awatara
     * @throws IllegalArgumentException gdy plik jest pusty, za duży lub ma niedozwolony format
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional
    public UserResponse uploadAvatar(Integer id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Plik jest pusty");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Plik jest za duży. Maksymalny rozmiar to 5 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equals("image/jpeg")
                        && !contentType.equals("image/png")
                        && !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException(
                    "Niedozwolony format pliku. Dozwolone są tylko JPG i PNG.");
        }

        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Nie znaleziono szukanego użytkownika"));

        try {
            byte[] fileBytes = file.getBytes();
            if (!hasValidImageSignature(fileBytes)) {
                throw new IllegalArgumentException(
                        "Zawartość pliku nie jest prawidłowym obrazem JPG ani PNG.");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get("uploads/avatars");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            deleteAvatarFileIfPresent(user.getAvatarUrl());

            Path filePath = uploadPath.resolve(newFilename);
            Files.write(filePath, fileBytes);

            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatarUrl(avatarUrl);
            return userMapper.toResponse(userRepository.save(user));
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas zapisywania awatara", e);
        }
    }

    /**
     * Weryfikuje, czy zawartość pliku zaczyna się od sygnatury (magic bytes) obrazu JPG lub PNG.
     * Chroni przed podszyciem się pod obraz przez sfałszowany nagłówek {@code Content-Type}.
     *
     * @param bytes zawartość przesłanego pliku
     * @return {@code true} jeśli plik jest obrazem JPG lub PNG
     */
    private boolean hasValidImageSignature(byte[] bytes) {
        if (bytes.length < 4) {
            return false;
        }
        boolean jpeg =
                (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF;
        boolean png =
                (bytes[0] & 0xFF) == 0x89
                        && (bytes[1] & 0xFF) == 0x50
                        && (bytes[2] & 0xFF) == 0x4E
                        && (bytes[3] & 0xFF) == 0x47;
        return jpeg || png;
    }

    /**
     * Usuwa awatar użytkownika — zarówno plik z dysku, jak i URL z bazy danych.
     *
     * @param id identyfikator użytkownika
     * @return zaktualizowane dane użytkownika bez awatara
     * @throws ResourceNotFoundException gdy użytkownik nie istnieje
     */
    @Transactional
    public UserResponse deleteAvatar(Integer id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Nie znaleziono użytkownika"));

        deleteAvatarFileIfPresent(user.getAvatarUrl());
        user.setAvatarUrl(null);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Usuwa plik awatara z dysku, jeśli istnieje. Ignoruje błędy I/O. Weryfikuje, że ścieżka pliku
     * mieści się w katalogu {@code uploads/avatars} (zabezpieczenie przed path traversal).
     *
     * @param avatarUrl relatywny URL awatara przechowywany w bazie danych
     */
    private void deleteAvatarFileIfPresent(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return;
        }
        try {
            String relativePath = avatarUrl;
            if (relativePath.startsWith("/uploads/avatars/")) {
                relativePath = relativePath.substring("/uploads/avatars/".length());
            }
            Path path = Paths.get("uploads/avatars").resolve(relativePath).normalize();
            if (!path.startsWith(Paths.get("uploads/avatars").normalize())) {
                return;
            }
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
