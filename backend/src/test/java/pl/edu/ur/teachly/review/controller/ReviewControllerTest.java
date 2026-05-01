package pl.edu.ur.teachly.review.controller;

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
import pl.edu.ur.teachly.review.dto.request.ReviewRequest;
import pl.edu.ur.teachly.review.dto.response.ReviewResponse;
import pl.edu.ur.teachly.review.service.ReviewService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController – testy jednostkowe")
class ReviewControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock private ReviewService reviewService;

    @InjectMocks private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void getTutorReviews() throws Exception {
        when(reviewService.getTutorReviews(1)).thenReturn(List.of());
        mockMvc.perform(get("/api/reviews/tutor/1")).andExpect(status().isOk());
    }

    @Test
    void addReview() throws Exception {
        ReviewRequest req = new ReviewRequest(1, java.math.BigDecimal.valueOf(5), "Super");
        when(reviewService.addReview(eq(1), any()))
                .thenReturn(
                        new ReviewResponse(
                                1,
                                1,
                                "A",
                                "B",
                                1,
                                "C",
                                "D",
                                java.math.BigDecimal.valueOf(5),
                                "Super",
                                null,
                                null));

        mockMvc.perform(
                        post("/api/reviews/student/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateReview() throws Exception {
        ReviewRequest req = new ReviewRequest(1, java.math.BigDecimal.valueOf(4), "OK");
        when(reviewService.updateReview(eq(1), any()))
                .thenReturn(
                        new ReviewResponse(
                                1,
                                1,
                                "A",
                                "B",
                                1,
                                "C",
                                "D",
                                java.math.BigDecimal.valueOf(4),
                                "OK",
                                null,
                                null));

        mockMvc.perform(
                        put("/api/reviews/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReview() throws Exception {
        mockMvc.perform(delete("/api/reviews/1")).andExpect(status().isNoContent());
        verify(reviewService).deleteReview(1);
    }
}
