package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.TimetableDayResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityOverrideResponse
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringRequest
import pl.edu.ur.teachly.data.model.TutorAvailabilityRecurringResponse
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.model.TutorSearchResultResponse
import pl.edu.ur.teachly.data.model.TutorSelfProfileRequest
import pl.edu.ur.teachly.data.model.TutorSubjectRequest
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.data.remote.TutorApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class TutorRepository(private val api: TutorApiService) {

    suspend fun searchTutors(
        query: String? = null,
        subject: String? = null,
        city: String? = null
    ): Result<List<TutorSearchResultResponse>> =
        apiCall("Błąd wyszukiwania korepetytorów") { api.searchTutors(query, subject, city) }

    suspend fun getAllTutors(query: String? = null): Result<List<TutorResponse>> =
        apiCall("Błąd pobierania korepetytorów") { api.getAllTutors(query) }

    suspend fun getTutorById(id: Int): Result<TutorResponse> =
        apiCall("Nie znaleziono korepetytora") { api.getTutorById(id) }

    suspend fun getTutorSubjects(id: Int): Result<List<TutorSubjectResponse>> =
        apiCall("Błąd pobierania przedmiotów") { api.getTutorSubjects(id) }

    suspend fun getTimetable(tutorId: Int, from: String, to: String): Result<List<TimetableDayResponse>> =
        apiCall("Błąd pobierania harmonogramu") { api.getTimetable(tutorId, from, to) }

    suspend fun updateMyProfile(request: TutorSelfProfileRequest): Result<TutorResponse> =
        apiCall("Błąd zapisywania profilu") { api.updateMyProfile(request) }

    suspend fun addMySubject(request: TutorSubjectRequest): Result<TutorSubjectResponse> =
        apiCall("Błąd dodawania przedmiotu") { api.addMySubject(request) }

    suspend fun removeMySubject(id: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania przedmiotu") { api.removeMySubject(id) }

    suspend fun adminUpdateTutor(id: Int, request: TutorRequest): Result<TutorResponse> =
        apiCall("Błąd aktualizacji korepetytora") { api.adminUpdateTutor(id, request) }

    suspend fun adminAddTutorSubject(tutorId: Int, request: TutorSubjectRequest): Result<TutorSubjectResponse> =
        apiCall("Błąd dodawania przedmiotu") { api.adminAddTutorSubject(tutorId, request) }

    suspend fun adminRemoveTutorSubject(tutorId: Int, tutorSubjectId: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania przedmiotu") { api.adminRemoveTutorSubject(tutorId, tutorSubjectId) }

    suspend fun getRecurringAvailability(tutorId: Int): Result<List<TutorAvailabilityRecurringResponse>> =
        apiCall("Błąd pobierania harmonogramu") { api.getRecurringAvailability(tutorId) }

    suspend fun addRecurringAvailability(
        tutorId: Int,
        request: TutorAvailabilityRecurringRequest
    ): Result<TutorAvailabilityRecurringResponse> =
        apiCall("Błąd dodawania slotu") { api.addRecurringAvailability(tutorId, request) }

    suspend fun deleteRecurringAvailability(tutorId: Int, id: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania slotu") { api.deleteRecurringAvailability(tutorId, id) }

    suspend fun getOverrides(tutorId: Int): Result<List<TutorAvailabilityOverrideResponse>> =
        apiCall("Błąd pobierania niedostępności") { api.getOverrides(tutorId) }

    suspend fun addOverride(
        tutorId: Int,
        request: TutorAvailabilityOverrideRequest
    ): Result<TutorAvailabilityOverrideResponse> =
        apiCall("Błąd dodawania niedostępności") { api.addOverride(tutorId, request) }

    suspend fun deleteOverride(tutorId: Int, id: Int): Result<Unit> =
        apiCallUnit("Błąd usuwania niedostępności") { api.deleteOverride(tutorId, id) }
}
