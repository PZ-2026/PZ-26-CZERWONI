package pl.edu.ur.teachly.data.repository;

import pl.edu.ur.teachly.data.model.TimetableDayResponse
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.data.remote.TutorApiService

class TutorRepository(private val api: TutorApiService) {

    suspend fun getAllTutors(): Result<List<TutorResponse>> {
        return try {
            val response = api.getAllTutors()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania korepetytorów"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun getTutorById(id: Int): Result<TutorResponse> {
        return try {
            val response = api.getTutorById(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Nie znaleziono korepetytora"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun getTutorSubjects(id: Int): Result<List<TutorSubjectResponse>> {
        return try {
            val response = api.getTutorSubjects(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania przedmiotów"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun getTimetable(
        tutorId: Int,
        from: String,
        to: String
    ): Result<List<TimetableDayResponse>> {
        return try {
            val response = api.getTimetable(tutorId, from, to)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania harmonogramu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}

