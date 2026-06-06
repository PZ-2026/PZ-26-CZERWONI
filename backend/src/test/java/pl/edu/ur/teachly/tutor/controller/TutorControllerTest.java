package pl.edu.ur.teachly.tutor.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import pl.edu.ur.teachly.tutor.dto.request.TutorSubjectRequest;
import pl.edu.ur.teachly.tutor.dto.response.TutorResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSearchResultResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorSubjectResponse;
import pl.edu.ur.teachly.tutor.service.TutorService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorController – testy jednostkowe")
class TutorControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private TutorService tutorService;

    @InjectMocks private TutorController tutorController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tutorController).build();
    }

    @Test
    @DisplayName("GET /api/tutors - zwraca listę")
    void getAllTutors() throws Exception {
        when(tutorService.getAllTutors(null))
                .thenReturn(
                        List.of(
                                new TutorResponse(
                                        1,
                                        "A",
                                        "B",
                                        "C",
                                        "123",
                                        "url",
                                        "Bio",
                                        java.math.BigDecimal.TEN,
                                        true,
                                        true)));

        mockMvc.perform(get("/api/tutors")).andExpect(status().isOk());

        verify(tutorService).getAllTutors(null);
    }

    @Test
    @DisplayName("GET /api/tutors/search - zwraca wyniki wyszukiwania")
    void searchTutors() throws Exception {
        when(tutorService.searchTutors(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/tutors/search")).andExpect(status().isOk());

        verify(tutorService).searchTutors(null, null);
    }

    @Test
    @DisplayName("GET /api/tutors/{id} - zwraca pojedynczego tutora")
    void getTutorById() throws Exception {
        when(tutorService.getTutorById(1))
                .thenReturn(
                        new TutorResponse(
                                1,
                                "A",
                                "B",
                                "C",
                                "123",
                                "url",
                                "Bio",
                                java.math.BigDecimal.TEN,
                                true,
                                true));

        mockMvc.perform(get("/api/tutors/1")).andExpect(status().isOk());

        verify(tutorService).getTutorById(1);
    }

    @Test
    @DisplayName("GET /api/tutors/{id}/subjects - zwraca przedmioty")
    void getTutorSubjects() throws Exception {
        when(tutorService.getTutorSubjects(1))
                .thenReturn(
                        List.of(
                                new TutorSubjectResponse(
                                        1, 1, "Mat", "Kat", true, false, false, false, false)));

        mockMvc.perform(get("/api/tutors/1/subjects")).andExpect(status().isOk());

        verify(tutorService).getTutorSubjects(1);
    }

    @Test
    @DisplayName("POST /api/tutors/{tutorId}/admin/subjects - dodaje przedmiot")
    void adminAddSubject() throws Exception {
        TutorSubjectRequest request =
                new TutorSubjectRequest(2, true, false, false, false, false);
        TutorSubjectResponse response =
                new TutorSubjectResponse(10, 2, "Fizyka", "Kat", true, false, false, false, false);

        when(tutorService.adminAddSubject(1, request)).thenReturn(response);

        mockMvc.perform(
                        post("/api/tutors/1/admin/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(tutorService).adminAddSubject(1, request);
    }

    @Test
    @DisplayName("DELETE /api/tutors/{tutorId}/admin/subjects/{id} - usuwa przedmiot")
    void adminRemoveSubject() throws Exception {
        mockMvc.perform(delete("/api/tutors/1/admin/subjects/10")).andExpect(status().isNoContent());

        verify(tutorService).adminRemoveSubject(1, 10);
    }
}
