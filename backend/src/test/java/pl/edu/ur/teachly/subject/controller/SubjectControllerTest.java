package pl.edu.ur.teachly.subject.controller;

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
import pl.edu.ur.teachly.subject.dto.request.SubjectCategoryRequest;
import pl.edu.ur.teachly.subject.dto.request.SubjectRequest;
import pl.edu.ur.teachly.subject.dto.response.SubjectCategoryResponse;
import pl.edu.ur.teachly.subject.dto.response.SubjectResponse;
import pl.edu.ur.teachly.subject.service.SubjectService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectController – testy jednostkowe")
class SubjectControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController).build();
    }

    @Test
    void getAllSubjects() throws Exception {
        when(subjectService.getAllSubjects()).thenReturn(List.of(new SubjectResponse(1, "Matematyka", 1, "Nauki ścisłe")));
        mockMvc.perform(get("/api/subjects")).andExpect(status().isOk());
    }

    @Test
    void addSubject() throws Exception {
        SubjectRequest req = new SubjectRequest("Fizyka", 1);
        when(subjectService.addSubject(any())).thenReturn(new SubjectResponse(2, "Fizyka", 1, "Nauki ścisłe"));
        mockMvc.perform(post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteSubject() throws Exception {
        mockMvc.perform(delete("/api/subjects/1")).andExpect(status().isNoContent());
        verify(subjectService).deleteSubject(1);
    }

    @Test
    void getAllCategories() throws Exception {
        when(subjectService.getAllCategories()).thenReturn(List.of(new SubjectCategoryResponse(1, "N")));
        mockMvc.perform(get("/api/subjects/categories")).andExpect(status().isOk());
    }

    @Test
    void addSubjectCategory() throws Exception {
        SubjectCategoryRequest req = new SubjectCategoryRequest("Nowa");
        when(subjectService.addSubjectCategory(any())).thenReturn(new SubjectCategoryResponse(2, "Nowa"));
        mockMvc.perform(post("/api/subjects/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteSubjectCategory() throws Exception {
        mockMvc.perform(delete("/api/subjects/categories/1")).andExpect(status().isNoContent());
        verify(subjectService).deleteSubjectCategory(1);
    }
}
