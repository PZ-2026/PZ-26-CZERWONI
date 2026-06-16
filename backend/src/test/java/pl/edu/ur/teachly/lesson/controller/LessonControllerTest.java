package pl.edu.ur.teachly.lesson.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.edu.ur.teachly.common.enums.UserRole;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.service.LessonService;
import pl.edu.ur.teachly.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonController – testy jednostkowe")
class LessonControllerTest {

    private MockMvc mockMvc;

    @Mock private LessonService lessonService;

    @InjectMocks private LessonController lessonController;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1).userRole(UserRole.STUDENT).isActive(true).build();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc =
                MockMvcBuilders.standaloneSetup(lessonController)
                        .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                        .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createLesson() throws Exception {
        when(lessonService.createLesson(eq(1), any())).thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        post("/api/lessons/student/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"tutorId\":2,\"subjectId\":1,"
                                                + "\"lessonDate\":\"2025-01-01\","
                                                + "\"timeFrom\":\"10:00\",\"timeTo\":\"11:00\","
                                                + "\"format\":\"ONLINE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void getStudentLessons() throws Exception {
        when(lessonService.getStudentLessons(1)).thenReturn(List.of());
        mockMvc.perform(get("/api/lessons/student/1")).andExpect(status().isOk());
        verify(lessonService).getStudentLessons(1);
    }

    @Test
    void getTutorLessons() throws Exception {
        when(lessonService.getTutorLessons(1)).thenReturn(List.of());
        mockMvc.perform(get("/api/lessons/tutor/1")).andExpect(status().isOk());
        verify(lessonService).getTutorLessons(1);
    }

    @Test
    void changeLessonStatus() throws Exception {
        when(lessonService.changeLessonStatus(eq(1), any())).thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        patch("/api/lessons/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"lessonStatus\":\"CONFIRMED\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllLessons() throws Exception {
        when(lessonService.searchLessons(any(), any(), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/lessons")).andExpect(status().isOk());
        verify(lessonService).searchLessons(null, null, null, null, null);
    }

    @Test
    void getLesson() throws Exception {
        when(lessonService.getLesson(1)).thenReturn(mock(LessonResponse.class));
        mockMvc.perform(get("/api/lessons/1")).andExpect(status().isOk());
        verify(lessonService).getLesson(1);
    }

    @Test
    void adminUpdateLesson() throws Exception {
        when(lessonService.adminUpdateLesson(eq(1), any())).thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        put("/api/lessons/1/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"lessonDate\":\"2025-08-01\","
                                                + "\"timeFrom\":\"10:00\",\"timeTo\":\"11:00\","
                                                + "\"format\":\"ONLINE\","
                                                + "\"lessonStatus\":\"CONFIRMED\","
                                                + "\"paymentStatus\":\"PAID\","
                                                + "\"amount\":50.00}"))
                .andExpect(status().isOk());
        verify(lessonService).adminUpdateLesson(eq(1), any());
    }

    @Test
    void updateStudentNotes() throws Exception {
        when(lessonService.updateStudentNotes(eq(1), any(), eq(1)))
                .thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        patch("/api/lessons/1/student-notes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"studentNotes\":\"Moje notatki\"}"))
                .andExpect(status().isOk());
        verify(lessonService).updateStudentNotes(eq(1), any(), eq(1));
    }

    @Test
    void updateTutorNotes() throws Exception {
        when(lessonService.updateTutorNotes(eq(1), any(), eq(1)))
                .thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        patch("/api/lessons/1/tutor-notes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"tutorNotes\":\"Notatki tutora\"}"))
                .andExpect(status().isOk());
        verify(lessonService).updateTutorNotes(eq(1), any(), eq(1));
    }

    @Test
    void updatePaymentStatus() throws Exception {
        when(lessonService.updatePaymentStatus(eq(1), any()))
                .thenReturn(mock(LessonResponse.class));

        mockMvc.perform(
                        patch("/api/lessons/1/payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"paymentStatus\":\"PAID\"}"))
                .andExpect(status().isOk());
        verify(lessonService).updatePaymentStatus(eq(1), any());
    }
}
