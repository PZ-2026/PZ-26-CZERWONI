package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.SubjectCategoryRequest
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.data.model.SubjectRequest
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.remote.SubjectApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class SubjectRepository(private val api: SubjectApiService) {

    suspend fun getAllSubjects(): Result<List<SubjectResponse>> =
        apiCall("Błąd pobierania przedmiotów") { api.getAllSubjects() }

    suspend fun getAllCategories(): Result<List<SubjectCategoryResponse>> =
        apiCall("Błąd pobierania kategorii") { api.getAllCategories() }

    suspend fun addSubject(request: SubjectRequest): Result<SubjectResponse> =
        apiCall("Błąd dodawania przedmiotu") { api.addSubject(request) }

    suspend fun updateSubject(id: Int, request: SubjectRequest): Result<SubjectResponse> =
        apiCall("Błąd aktualizacji przedmiotu") { api.updateSubject(id, request) }

    suspend fun deleteSubject(id: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania przedmiotu") { api.deleteSubject(id) }

    suspend fun addCategory(request: SubjectCategoryRequest): Result<SubjectCategoryResponse> =
        apiCall("Błąd dodawania kategorii") { api.addCategory(request) }

    suspend fun updateCategory(id: Int, request: SubjectCategoryRequest): Result<SubjectCategoryResponse> =
        apiCall("Błąd aktualizacji kategorii") { api.updateCategory(id, request) }

    suspend fun deleteCategory(id: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania kategorii") { api.deleteCategory(id) }
}
