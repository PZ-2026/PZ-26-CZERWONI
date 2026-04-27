package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.SubjectCategoryRequest
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.data.model.SubjectRequest
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.repository.SubjectRepository

data class AdminSubjectsState(
    val subjects: List<SubjectResponse> = emptyList(),
    val categories: List<SubjectCategoryResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminSubjectsViewModel(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminSubjectsState())
    val state: StateFlow<AdminSubjectsState> = _state.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val subjects = subjectRepository.getAllSubjects().getOrElse { emptyList() }
            val categories = subjectRepository.getAllCategories().getOrElse { emptyList() }
            _state.update { it.copy(subjects = subjects, categories = categories, isLoading = false) }
        }
    }

    fun addSubject(name: String, categoryId: Int) {
        viewModelScope.launch {
            subjectRepository.addSubject(SubjectRequest(name, categoryId)).fold(
                onSuccess = { subject ->
                    _state.update { s ->
                        s.copy(subjects = s.subjects + subject, successMessage = "Przedmiot został dodany")
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateSubject(id: Int, name: String, categoryId: Int) {
        viewModelScope.launch {
            subjectRepository.updateSubject(id, SubjectRequest(name, categoryId)).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            subjects = s.subjects.map { if (it.id == id) updated else it },
                            successMessage = "Przedmiot został zaktualizowany"
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun deleteSubject(id: Int) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(id).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(subjects = s.subjects.filter { it.id != id }, successMessage = "Przedmiot został usunięty")
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            subjectRepository.addCategory(SubjectCategoryRequest(name)).fold(
                onSuccess = { category ->
                    _state.update { s ->
                        s.copy(categories = s.categories + category, successMessage = "Kategoria została dodana")
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateCategory(id: Int, name: String) {
        viewModelScope.launch {
            subjectRepository.updateCategory(id, SubjectCategoryRequest(name)).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            categories = s.categories.map { if (it.id == id) updated else it },
                            successMessage = "Kategoria została zaktualizowana"
                        )
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            subjectRepository.deleteCategory(id).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(categories = s.categories.filter { it.id != id }, successMessage = "Kategoria została usunięta")
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
