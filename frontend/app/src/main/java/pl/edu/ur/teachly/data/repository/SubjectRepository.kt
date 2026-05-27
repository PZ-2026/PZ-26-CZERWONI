package pl.edu.ur.teachly.data.repository;

import pl.edu.ur.teachly.data.model.SubjectCategoryRequest
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.data.model.SubjectRequest
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.remote.SubjectApiService

class SubjectRepository(private val api: SubjectApiService) {
    suspend fun getAllSubjects(): Result<List<SubjectResponse>> {
        return try {
            val response = api.getAllSubjects()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania przedmiotów"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun getAllCategories(): Result<List<SubjectCategoryResponse>> {
        return try {
            val response = api.getAllCategories()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania kategorii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun addSubject(request: SubjectRequest): Result<SubjectResponse> {
        return try {
            val response = api.addSubject(request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd dodawania przedmiotu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun updateSubject(id: Int, request: SubjectRequest): Result<SubjectResponse> {
        return try {
            val response = api.updateSubject(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji przedmiotu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deleteSubject(id: Int): Result<Unit> {
        return try {
            val response = api.deleteSubject(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usuwania przedmiotu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun addCategory(request: SubjectCategoryRequest): Result<SubjectCategoryResponse> {
        return try {
            val response = api.addCategory(request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd dodawania kategorii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun updateCategory(
        id: Int,
        request: SubjectCategoryRequest
    ): Result<SubjectCategoryResponse> {
        return try {
            val response = api.updateCategory(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji kategorii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deleteCategory(id: Int): Result<Unit> {
        return try {
            val response = api.deleteCategory(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usuwania kategorii"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}
