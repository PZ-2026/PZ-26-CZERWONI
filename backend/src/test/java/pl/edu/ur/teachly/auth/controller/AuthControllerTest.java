package pl.edu.ur.teachly.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.edu.ur.teachly.auth.dto.request.LoginRequest;
import pl.edu.ur.teachly.auth.dto.request.RegisterRequest;
import pl.edu.ur.teachly.auth.dto.response.AuthResponse;
import pl.edu.ur.teachly.auth.service.AuthService;
import pl.edu.ur.teachly.common.enums.UserRole;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController – testy jednostkowe")
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register() throws Exception {
        RegisterRequest req = new RegisterRequest(UserRole.STUDENT, "Student", "B", "email@test.com", "123456789", "password123");
        when(authService.register(any())).thenReturn(new AuthResponse("token", UserRole.STUDENT, 1));
        
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void login() throws Exception {
        LoginRequest req = new LoginRequest("email@test.com", "pass");
        when(authService.login(any())).thenReturn(new AuthResponse("token", UserRole.STUDENT, 1));
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
