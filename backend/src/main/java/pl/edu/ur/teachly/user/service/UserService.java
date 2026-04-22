package pl.edu.ur.teachly.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.teachly.common.exception.ResourceNotFoundException;
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
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deactivateUser(Integer id) {
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
}
