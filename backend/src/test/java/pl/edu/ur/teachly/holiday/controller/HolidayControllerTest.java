package pl.edu.ur.teachly.holiday.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import pl.edu.ur.teachly.holiday.dto.response.HolidayResponse;
import pl.edu.ur.teachly.holiday.service.HolidayService;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidayController – testy jednostkowe")
class HolidayControllerTest {

    private MockMvc mockMvc;

    @Mock private HolidayService holidayService;

    @InjectMocks private HolidayController holidayController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController).build();
    }

    @Test
    void getAllHolidays() throws Exception {
        when(holidayService.getAllHolidays()).thenReturn(List.of());
        mockMvc.perform(get("/api/holidays")).andExpect(status().isOk());
    }

    @Test
    void addHoliday() throws Exception {
        when(holidayService.addHoliday(any()))
                .thenReturn(new HolidayResponse(1, LocalDate.now(), "Nazwa"));

        mockMvc.perform(
                        post("/api/holidays")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"holidayDate\":\"2026-01-01\",\"description\":\"Nazwa\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateHoliday() throws Exception {
        when(holidayService.updateHoliday(eq(1), any()))
                .thenReturn(new HolidayResponse(1, LocalDate.now(), "Nazwa2"));

        mockMvc.perform(
                        put("/api/holidays/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"holidayDate\":\"2026-01-01\",\"description\":\"Nazwa2\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteHoliday() throws Exception {
        mockMvc.perform(delete("/api/holidays/1")).andExpect(status().isNoContent());
        verify(holidayService).deleteHoliday(1);
    }
}
