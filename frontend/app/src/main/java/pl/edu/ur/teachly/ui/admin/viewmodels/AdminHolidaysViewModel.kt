package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.HolidayRequest
import pl.edu.ur.teachly.data.model.HolidayResponse
import pl.edu.ur.teachly.data.repository.HolidayRepository

data class AdminHolidaysState(
    val holidays: List<HolidayResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminHolidaysViewModel(
    private val holidayRepository: HolidayRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminHolidaysState())
    val state: StateFlow<AdminHolidaysState> = _state.asStateFlow()

    init {
        loadHolidays()
    }

    fun loadHolidays() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            holidayRepository.getAllHolidays().fold(
                onSuccess = { holidays -> _state.update { it.copy(holidays = holidays.sortedBy { h -> h.holidayDate }, isLoading = false) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun addHoliday(date: String, description: String?) {
        viewModelScope.launch {
            holidayRepository.addHoliday(HolidayRequest(date, description)).fold(
                onSuccess = { holiday ->
                    _state.update { s ->
                        s.copy(
                            holidays = (s.holidays + holiday).sortedBy { it.holidayDate },
                            successMessage = "Święto zostało dodane"
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateHoliday(id: Int, date: String, description: String?) {
        viewModelScope.launch {
            holidayRepository.updateHoliday(id, HolidayRequest(date, description)).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            holidays = s.holidays.map { if (it.id == id) updated else it }.sortedBy { it.holidayDate },
                            successMessage = "Święto zostało zaktualizowane"
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun deleteHoliday(id: Int) {
        viewModelScope.launch {
            holidayRepository.deleteHoliday(id).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(
                            holidays = s.holidays.filter { it.id != id },
                            successMessage = "Święto zostało usunięte"
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
