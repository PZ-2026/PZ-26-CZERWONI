package pl.edu.ur.teachly.ui.availability.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse
import pl.edu.ur.teachly.data.repository.TutorRepository

data class AvailabilityUiState(
    val recurring: List<TutorAvailabilityRecurringResponse> = emptyList(),
    val overrides: List<TutorAvailabilityOverrideResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null,
)

class AvailabilityViewModel(
    private val tutorRepository: TutorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AvailabilityUiState())
    val state: StateFlow<AvailabilityUiState> = _state.asStateFlow()

    fun load(tutorId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val recurring = tutorRepository.getRecurringAvailability(tutorId).getOrElse { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }
            val overrides = tutorRepository.getOverrides(tutorId).getOrDefault(emptyList())

            _state.update {
                it.copy(
                    recurring = recurring,
                    overrides = overrides,
                    isLoading = false
                )
            }
        }
    }

    fun addSlot(tutorId: Int, dayOfWeek: Int, timeFrom: String, timeTo: String) {
        viewModelScope.launch {
            tutorRepository.addRecurringAvailability(
                tutorId,
                TutorAvailabilityRecurringRequest(dayOfWeek, timeFrom, timeTo, null),
            ).fold(
                onSuccess = { slot ->
                    _state.update {
                        it.copy(
                            recurring = it.recurring + slot,
                            successMessage = "Slot dostępności dodany",
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } },
            )
        }
    }

    fun deleteSlot(tutorId: Int, slotId: Int) {
        viewModelScope.launch {
            tutorRepository.deleteRecurringAvailability(tutorId, slotId).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            recurring = it.recurring.filter { s -> s.id != slotId },
                            successMessage = "Slot usunięty",
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } },
            )
        }
    }

    fun addOverride(tutorId: Int, date: String, timeFrom: String? = null, timeTo: String? = null) {
        viewModelScope.launch {
            tutorRepository.addOverride(
                tutorId,
                TutorAvailabilityOverrideRequest(date, timeFrom, timeTo),
            ).fold(
                onSuccess = { override ->
                    _state.update {
                        it.copy(
                            overrides = it.overrides + override,
                            successMessage = "Niedostępność dodana",
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } },
            )
        }
    }

    fun deleteOverride(tutorId: Int, overrideId: Int) {
        viewModelScope.launch {
            tutorRepository.deleteOverride(tutorId, overrideId).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            overrides = it.overrides.filter { o -> o.id != overrideId },
                            successMessage = "Niedostępność usunięta",
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } },
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(successMessage = null, error = null) }
    }
}
