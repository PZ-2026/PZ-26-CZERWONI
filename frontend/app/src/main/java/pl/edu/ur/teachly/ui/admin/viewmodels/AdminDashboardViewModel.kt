package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.AdminStatsResponse
import pl.edu.ur.teachly.data.repository.AdminRepository

data class AdminDashboardState(
    val stats: AdminStatsResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AdminDashboardViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            adminRepository.getStats().fold(
                onSuccess = { stats -> _state.update { it.copy(stats = stats, isLoading = false) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }
}
