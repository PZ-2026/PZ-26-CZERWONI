package pl.edu.ur.teachly.data.repository;

import pl.edu.ur.teachly.data.model.TimetableDayResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse
import pl.edu.ur.teachly.data.model.TutorRequest
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

    suspend fun adminUpdateTutor(id: Int, request: TutorRequest): Result<TutorResponse> {
        return try {
            val response = api.adminUpdateTutor(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji korepetytora"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    // Recurring availability
    suspend fun getRecurringAvailability(tutorId: Int): Result<List<TutorAvailabilityRecurringResponse>> =
        try {
            val r = api.getRecurringAvailability(tutorId)
            if (r.isSuccessful) Result.success(r.body()!!)
            else Result.failure(Exception("Błąd pobierania harmonogramu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }

    suspend fun addRecurringAvailability(
        tutorId: Int,
        request: TutorAvailabilityRecurringRequest,
    ): Result<TutorAvailabilityRecurringResponse> =
        try {
            val r = api.addRecurringAvailability(tutorId, request)
            if (r.isSuccessful) Result.success(r.body()!!)
            else Result.failure(Exception("Błąd dodawania slotu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }

    suspend fun deleteRecurringAvailability(tutorId: Int, id: Int): Result<Unit> =
        try {
            val r = api.deleteRecurringAvailability(tutorId, id)
            if (r.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usuwania slotu"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }

    // Overrides (specific-date unavailability)
    suspend fun getOverrides(tutorId: Int): Result<List<TutorAvailabilityOverrideResponse>> =
        try {
            val r = api.getOverrides(tutorId)
            if (r.isSuccessful) Result.success(r.body()!!)
            else Result.failure(Exception("Błąd pobierania niedostępności"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }

    suspend fun addOverride(
        tutorId: Int,
        request: TutorAvailabilityOverrideRequest,
    ): Result<TutorAvailabilityOverrideResponse> =
        try {
            val r = api.addOverride(tutorId, request)
            if (r.isSuccessful) Result.success(r.body()!!)
            else Result.failure(Exception("Błąd dodawania niedostępności"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }

    suspend fun deleteOverride(tutorId: Int, id: Int): Result<Unit> =
        try {
            val r = api.deleteOverride(tutorId, id)
            if (r.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usuwania niedostępności"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
}

