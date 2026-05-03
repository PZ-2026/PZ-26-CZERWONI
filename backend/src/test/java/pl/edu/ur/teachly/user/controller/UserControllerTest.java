package pl.edu.ur.teachly.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.user.dto.request.UserUpdateRequest;
import pl.edu.ur.teachly.user.dto.response.UserResponse;
import pl.edu.ur.teachly.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController – testy jednostkowe")
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(1))
                .thenReturn(
                        new UserResponse(
                                1, "A", "B", "C", "D", "E", UserRole.STUDENT, true, null, null));
        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk());
    }

    @Test
    void updateUserProfile() throws Exception {
        UserUpdateRequest req = new UserUpdateRequest("A", "B", "C");
        when(userService.updateUserProfile(eq(1), any()))
                .thenReturn(
                        new UserResponse(
                                1, "A", "B", "C", "D", "E", UserRole.STUDENT, true, null, null));

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateUser() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
        verify(userService).deactivateUser(1);
    }
}
