package pl.edu.ur.teachly.tutor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
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
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityOverrideRequest;
import pl.edu.ur.teachly.tutor.dto.request.TutorAvailabilityRecurringRequest;
import pl.edu.ur.teachly.tutor.dto.response.TimetableDayResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityOverrideResponse;
import pl.edu.ur.teachly.tutor.dto.response.TutorAvailabilityRecurringResponse;
import pl.edu.ur.teachly.tutor.service.TimetableService;
import pl.edu.ur.teachly.tutor.service.TutorAvailabilityService;
import pl.edu.ur.teachly.user.entity.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorAvailabilityController – testy jednostkowe")
class TutorAvailabilityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private TutorAvailabilityService availabilityService;
    @Mock private TimetableService timetableService;

    @InjectMocks private TutorAvailabilityController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        User user = User.builder().id(1).userRole(UserRole.TUTOR).isActive(true).build();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                        .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── timetable ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /timetable – zwraca plan terminów")
    void getTimetable() throws Exception {
        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to = LocalDate.of(2025, 9, 7);
        when(timetableService.getTimetable(eq(1), eq(from), eq(to), any()))
                .thenReturn(List.of(new TimetableDayResponse(from, List.of())));

        mockMvc.perform(
                        get("/api/tutors/1/availability/timetable")
                                .param("from", "2025-09-01")
                                .param("to", "2025-09-07"))
                .andExpect(status().isOk());

        verify(timetableService).getTimetable(eq(1), eq(from), eq(to), any());
    }

    // ─── recurring ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /recurring – zwraca cykliczne sloty")
    void getRecurringByTutor() throws Exception {
        when(availabilityService.getRecurringByTutor(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/tutors/1/availability/recurring")).andExpect(status().isOk());

        verify(availabilityService).getRecurringByTutor(1);
    }

    @Test
    @DisplayName("POST /recurring – dodaje slot cykliczny")
    void addRecurring() throws Exception {
        TutorAvailabilityRecurringRequest req =
                new TutorAvailabilityRecurringRequest(
                        1, LocalTime.of(9, 0), LocalTime.of(17, 0), null);
        TutorAvailabilityRecurringResponse response =
                new TutorAvailabilityRecurringResponse(
                        10, 1, 1, LocalTime.of(9, 0), LocalTime.of(17, 0), null);

        when(availabilityService.addRecurring(eq(1), any())).thenReturn(response);

        mockMvc.perform(
                        post("/api/tutors/1/availability/recurring")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(availabilityService).addRecurring(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /recurring/{id} – usuwa slot cykliczny")
    void deleteRecurring() throws Exception {
        mockMvc.perform(delete("/api/tutors/1/availability/recurring/10"))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteRecurring(10, 1);
    }

    // ─── override ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /override – zwraca nadpisania dostępności")
    void getOverridesByTutor() throws Exception {
        when(availabilityService.getOverridesByTutor(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/tutors/1/availability/override")).andExpect(status().isOk());

        verify(availabilityService).getOverridesByTutor(1);
    }

    @Test
    @DisplayName("POST /override – dodaje jednorazowe nadpisanie")
    void addOverride() throws Exception {
        TutorAvailabilityOverrideRequest req =
                new TutorAvailabilityOverrideRequest(LocalDate.of(2025, 12, 24), null, null);
        TutorAvailabilityOverrideResponse response =
                new TutorAvailabilityOverrideResponse(
                        20, 1, LocalDate.of(2025, 12, 24), null, null);

        when(availabilityService.addOverride(eq(1), any())).thenReturn(response);

        mockMvc.perform(
                        post("/api/tutors/1/availability/override")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(availabilityService).addOverride(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /override/{id} – usuwa nadpisanie")
    void deleteOverride() throws Exception {
        mockMvc.perform(delete("/api/tutors/1/availability/override/20"))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteOverride(20, 1);
    }
}
