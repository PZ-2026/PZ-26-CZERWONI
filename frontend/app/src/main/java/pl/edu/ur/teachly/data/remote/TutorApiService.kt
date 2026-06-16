package pl.edu.ur.teachly.data.remote

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TutorApiService {

    @GET("api/tutors/search")
    suspend fun searchTutors(
        @Query("q") query: String? = null,
        @Query("subject") subject: String? = null,
        @Query("city") city: String? = null
    ): Response<List<TutorSearchResultResponse>>

    @GET("api/tutors")
    suspend fun getAllTutors(@Query("q") query: String? = null): Response<List<TutorResponse>>

    @GET("api/tutors/{id}")
    suspend fun getTutorById(@Path("id") id: Int): Response<TutorResponse>

    @GET("api/tutors/{tutorId}/subjects")
    suspend fun getTutorSubjects(@Path("tutorId") tutorId: Int): Response<List<TutorSubjectResponse>>

    @GET("api/tutors/{tutorId}/availability/timetable")
    suspend fun getTimetable(
        @Path("tutorId") tutorId: Int,
        // format: "yyyy-MM-dd"
        @Query("from") from: String,
        // format: "yyyy-MM-dd"
        @Query("to") to: String
    ): Response<List<TimetableDayResponse>>

    @GET("api/tutors/{tutorId}/availability/recurring")
    suspend fun getRecurringAvailability(
        @Path("tutorId") tutorId: Int
    ): Response<List<TutorAvailabilityRecurringResponse>>

    @POST("api/tutors/{tutorId}/availability/recurring")
    suspend fun addRecurringAvailability(
        @Path("tutorId") tutorId: Int,
        @Body request: TutorAvailabilityRecurringRequest
    ): Response<TutorAvailabilityRecurringResponse>

    @DELETE("api/tutors/{tutorId}/availability/recurring/{id}")
    suspend fun deleteRecurringAvailability(@Path("tutorId") tutorId: Int, @Path("id") id: Int): Response<Unit>

    @GET("api/tutors/{tutorId}/availability/override")
    suspend fun getOverrides(@Path("tutorId") tutorId: Int): Response<List<TutorAvailabilityOverrideResponse>>

    @POST("api/tutors/{tutorId}/availability/override")
    suspend fun addOverride(
        @Path("tutorId") tutorId: Int,
        @Body request: TutorAvailabilityOverrideRequest
    ): Response<TutorAvailabilityOverrideResponse>

    @DELETE("api/tutors/{tutorId}/availability/override/{id}")
    suspend fun deleteOverride(@Path("tutorId") tutorId: Int, @Path("id") id: Int): Response<Unit>

    @PUT("api/tutors/me")
    suspend fun updateMyProfile(@Body request: TutorSelfProfileRequest): Response<TutorResponse>

    @POST("api/tutors/me/subjects")
    suspend fun addMySubject(@Body request: TutorSubjectRequest): Response<TutorSubjectResponse>

    @DELETE("api/tutors/me/subjects/{id}")
    suspend fun removeMySubject(@Path("id") id: Int): Response<Unit>

    @PUT("api/tutors/{id}/admin")
    suspend fun adminUpdateTutor(@Path("id") id: Int, @Body request: TutorRequest): Response<TutorResponse>

    @POST("api/tutors/{tutorId}/admin/subjects")
    suspend fun adminAddTutorSubject(
        @Path("tutorId") tutorId: Int,
        @Body request: TutorSubjectRequest
    ): Response<TutorSubjectResponse>

    @DELETE("api/tutors/{tutorId}/admin/subjects/{tutorSubjectId}")
    suspend fun adminRemoveTutorSubject(
        @Path("tutorId") tutorId: Int,
        @Path("tutorSubjectId") tutorSubjectId: Int
    ): Response<Unit>
}
