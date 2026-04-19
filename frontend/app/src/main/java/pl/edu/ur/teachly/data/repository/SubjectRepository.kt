package pl.edu.ur.teachly.data.repository;

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
}
