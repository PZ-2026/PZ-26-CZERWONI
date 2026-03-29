package pl.edu.ur.teachly.ui.tutor.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.edu.ur.teachly.ui.components.MOCK_TUTORS
import pl.edu.ur.teachly.ui.components.Tutor

class TutorDetailViewModel : ViewModel() {

    private val _tutor = MutableStateFlow<Tutor?>(null)
    val tutor: StateFlow<Tutor?> = _tutor.asStateFlow()

    fun loadTutor(tutorId: String) {
        _tutor.value = MOCK_TUTORS.first { it.id.toString() == tutorId }
        // docelowo: viewModelScope.launch { _tutor.value = repository.getTutor(tutorId) }
    }
}