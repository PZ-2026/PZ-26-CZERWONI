package pl.edu.ur.teachly.lesson.controller;

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
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.lesson.dto.request.LessonRequest;
import pl.edu.ur.teachly.lesson.dto.request.LessonStatusRequest;
import pl.edu.ur.teachly.lesson.dto.response.LessonResponse;
import pl.edu.ur.teachly.lesson.service.LessonService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonController – testy jednostkowe")
class LessonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();
    }

    @Test
    void createLesson() throws Exception {
        when(lessonService.createLesson(eq(1), any())).thenReturn(mock(LessonResponse.class));
        
        mockMvc.perform(post("/api/lessons/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":1,\"date\":\"2025-01-01\"}"))
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
        
        mockMvc.perform(patch("/api/lessons/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk());
    }
}
