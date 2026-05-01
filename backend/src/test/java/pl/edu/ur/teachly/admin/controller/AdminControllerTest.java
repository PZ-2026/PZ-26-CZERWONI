package pl.edu.ur.teachly.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.edu.ur.teachly.admin.dto.response.AdminStatsResponse;
import pl.edu.ur.teachly.admin.service.AdminService;
import pl.edu.ur.teachly.review.service.ReviewService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController – testy jednostkowe")
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock private AdminService adminService;
    @Mock private ReviewService reviewService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void getStats() throws Exception {
        AdminStatsResponse stats = new AdminStatsResponse(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        when(adminService.getStats()).thenReturn(stats);
        
        mockMvc.perform(get("/api/admin/stats")).andExpect(status().isOk());
        verify(adminService).getStats();
    }

    @Test
    void getAllReviews() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/reviews")).andExpect(status().isOk());
        verify(reviewService).getAllReviews();
    }

    @Test
    void deleteReview() throws Exception {
        mockMvc.perform(delete("/api/admin/reviews/1")).andExpect(status().isNoContent());
        verify(reviewService).deleteReview(1);
    }
}
