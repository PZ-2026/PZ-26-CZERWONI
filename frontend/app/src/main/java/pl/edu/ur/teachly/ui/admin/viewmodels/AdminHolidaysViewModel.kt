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
    val filteredHolidays: List<HolidayResponse> = emptyList(),
    val availableYears: List<Int> = emptyList(),
    val selectedYear: Int? = null,
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
                onSuccess = { holidays ->
                    val sorted = holidays.sortedBy { it.holidayDate }
                    val years =
                        sorted.map { it.holidayDate.take(4).toIntOrNull() ?: 0 }.distinct().sorted()
                    _state.update {
                        it.copy(
                            holidays = sorted,
                            availableYears = years,
                            isLoading = false
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onYearFilterChange(year: Int?) {
        _state.update { it.copy(selectedYear = year) }
        applyFilters()
    }

    private fun applyFilters() {
        val year = _state.value.selectedYear
        val filtered = _state.value.holidays.filter { holiday ->
            year == null || holiday.holidayDate.take(4).toIntOrNull() == year
        }
        _state.update { it.copy(filteredHolidays = filtered) }
    }

    fun addHoliday(date: String, description: String?) {
        viewModelScope.launch {
            holidayRepository.addHoliday(HolidayRequest(date, description)).fold(
                onSuccess = { holiday ->
                    val updated = (_state.value.holidays + holiday).sortedBy { it.holidayDate }
                    val years = updated.map { it.holidayDate.take(4).toIntOrNull() ?: 0 }.distinct()
                        .sorted()
                    _state.update {
                        it.copy(
                            holidays = updated,
                            availableYears = years,
                            successMessage = "Święto zostało dodane"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateHoliday(id: Int, date: String, description: String?) {
        viewModelScope.launch {
            holidayRepository.updateHoliday(id, HolidayRequest(date, description)).fold(
                onSuccess = { updated ->
                    val holidays = _state.value.holidays.map { if (it.id == id) updated else it }
                        .sortedBy { it.holidayDate }
                    _state.update {
                        it.copy(
                            holidays = holidays,
                            successMessage = "Święto zostało zaktualizowane"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun deleteHoliday(id: Int) {
        viewModelScope.launch {
            holidayRepository.deleteHoliday(id).fold(
                onSuccess = {
                    val holidays = _state.value.holidays.filter { it.id != id }
                    val years =
                        holidays.map { it.holidayDate.take(4).toIntOrNull() ?: 0 }.distinct()
                            .sorted()
                    _state.update {
                        it.copy(
                            holidays = holidays,
                            availableYears = years,
                            successMessage = "Święto zostało usunięte"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
