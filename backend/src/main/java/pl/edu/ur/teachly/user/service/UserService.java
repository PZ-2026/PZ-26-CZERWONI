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
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.user.dto.request.AdminUserUpdateRequest;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

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
            Files.copy(file.getInputStream(), filePath);

            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatarUrl(avatarUrl);
            return userMapper.toResponse(userRepository.save(user));
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas zapisywania awatara", e);
        }
    }

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
