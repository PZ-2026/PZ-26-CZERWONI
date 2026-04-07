package pl.edu.ur.teachly.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateUserProfile(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUserProfile(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
    }
}
