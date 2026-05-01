package pl.edu.ur.teachly.tutor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.service.TutorService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorController – testy jednostkowe")
class TutorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TutorService tutorService;

    @InjectMocks
    private TutorController tutorController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tutorController).build();
    }

    @Test
    @DisplayName("GET /api/tutors - zwraca listę")
    void getAllTutors() throws Exception {
        when(tutorService.getAllTutors()).thenReturn(List.of(new TutorResponse(1, "A", "B", "C", "123", "url", "Bio", java.math.BigDecimal.TEN, true, true)));

        mockMvc.perform(get("/api/tutors"))
                .andExpect(status().isOk());

        verify(tutorService).getAllTutors();
    }

    @Test
    @DisplayName("GET /api/tutors/{id} - zwraca pojedynczego tutora")
    void getTutorById() throws Exception {
        when(tutorService.getTutorById(1)).thenReturn(new TutorResponse(1, "A", "B", "C", "123", "url", "Bio", java.math.BigDecimal.TEN, true, true));

        mockMvc.perform(get("/api/tutors/1"))
                .andExpect(status().isOk());

        verify(tutorService).getTutorById(1);
    }

    @Test
    @DisplayName("GET /api/tutors/{id}/subjects - zwraca przedmioty")
    void getTutorSubjects() throws Exception {
        when(tutorService.getTutorSubjects(1)).thenReturn(List.of(new TutorSubjectResponse(1, 1, "Mat", "Kat", true, false, false, false, false)));

        mockMvc.perform(get("/api/tutors/1/subjects"))
                .andExpect(status().isOk());

        verify(tutorService).getTutorSubjects(1);
    }
}
