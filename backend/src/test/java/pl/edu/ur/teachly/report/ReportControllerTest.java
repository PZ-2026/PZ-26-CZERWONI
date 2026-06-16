package pl.edu.ur.teachly.report;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.common.exception.GlobalExceptionHandler;
import pl.edu.ur.teachly.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportController – testy jednostkowe")
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock private ReportService reportService;

    @InjectMocks private ReportController reportController;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser =
                User.builder()
                        .id(1)
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .userRole(UserRole.STUDENT)
                        .isActive(true)
                        .build();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc =
                MockMvcBuilders.standaloneSetup(reportController)
                        .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/reports/my – generuje raport i zwraca PDF")
    void getMyReport_returnsOk() throws Exception {
        byte[] pdfBytes = new byte[] {37, 80, 68, 70};
        when(reportService.generateReport(any(), any(), any(), eq("LESSONS"), any()))
                .thenReturn(pdfBytes);

        mockMvc.perform(
                        get("/api/reports/my")
                                .param("startDate", "2025-01-01")
                                .param("endDate", "2025-12-31"))
                .andExpect(status().isOk());

        verify(reportService).generateReport(any(), any(), any(), eq("LESSONS"), any());
    }

    @Test
    @DisplayName("GET /api/reports/my – błąd serwisu zwraca 500")
    void getMyReport_serviceThrows_returns500() throws Exception {
        when(reportService.generateReport(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Błąd generowania"));

        mockMvc.perform(
                        get("/api/reports/my")
                                .param("startDate", "2025-01-01")
                                .param("endDate", "2025-12-31"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/reports/my – z typem REVENUE przekazuje typ do serwisu")
    void getMyReport_withType_passesTypeToService() throws Exception {
        byte[] pdfBytes = new byte[] {37, 80, 68, 70};
        when(reportService.generateReport(any(), any(), any(), eq("REVENUE"), any()))
                .thenReturn(pdfBytes);

        mockMvc.perform(
                        get("/api/reports/my")
                                .param("startDate", "2025-01-01")
                                .param("endDate", "2025-12-31")
                                .param("type", "REVENUE"))
                .andExpect(status().isOk());

        verify(reportService).generateReport(any(), any(), any(), eq("REVENUE"), any());
    }
}
