package pl.edu.ur.teachly.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.auth.dto.request.LoginRequest;
import pl.edu.ur.teachly.auth.dto.request.RegisterRequest;
import pl.edu.ur.teachly.auth.dto.response.AuthResponse;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.BusinessValidationException;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
import pl.edu.ur.teachly.user.entity.User;
import pl.edu.ur.teachly.user.mapper.UserMapper;
import pl.edu.ur.teachly.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessValidationException("Email jest już zajęty");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BusinessValidationException("Numer telefonu jest już zajęty");
        }

        User user = userMapper.toEntity(request);

        // TODO: Change after Spring Security implementation
        user.setPasswordHash(request.password());
        user.setRole(UserRole.STUDENT);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Nieprawidłowy e-mail lub hasło"));

        // TODO: Check password by AuthManager
        if (!user.getPasswordHash().equals(request.password())) {
            throw new BusinessValidationException("Nieprawidłowy e-mail lub hasło");
        }

        // TODO: Generate real JWT
        String placeholderToken = "ey.mock.jwt.token.for." + user.getEmail();

        return new AuthResponse(placeholderToken, user.getRole(), user.getId());
    }
}
