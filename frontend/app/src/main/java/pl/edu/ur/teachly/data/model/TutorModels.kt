package pl.edu.ur.teachly.data.model

data class TutorRequest(
    val bio: String?,
    val hourlyRate: Double,
    val offersOnline: Boolean,
    val offersInPerson: Boolean
)

data class TutorResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val avatarUrl: String?,
    val bio: String?,
    val hourlyRate: Double,
    val offersOnline: Boolean,
    val offersInPerson: Boolean
)

data class TutorSubjectRequest(
    val subjectId: Int,
    val levelPrimary: Boolean?,
    val levelHighSchool: Boolean?,
    val levelUniversity: Boolean?,
    val levelExamPrep: Boolean?,
    val levelProfessional: Boolean?
)

data class TutorSubjectResponse(
    val id: Int,
    val subjectId: Int,
    val subjectName: String,
    val categoryName: String,
    val levelPrimary: Boolean?,
    val levelHighSchool: Boolean?,
    val levelUniversity: Boolean?,
    val levelExamPrep: Boolean?,
    val levelProfessional: Boolean?
)

data class TutorAvailabilityRecurringRequest(
    val dayOfWeek: Int,
    val timeFrom: String,
    val timeTo: String,
    val dateTo: String?
)

data class TutorAvailabilityRecurringResponse(
    val id: Int,
    val tutorId: Int,
    val dayOfWeek: Int,
    val timeFrom: String,
    val timeTo: String,
    val dateTo: String?
)

data class TutorAvailabilityOverrideRequest(
    val overrideDate: String,
    val timeFrom: String?,
    val timeTo: String?
)

data class TutorAvailabilityOverrideResponse(
    val id: Int,
    val tutorId: Int,
    val overrideDate: String,
    val timeFrom: String?,
    val timeTo: String?
)

data class TimeSlot(
    val timeFrom: String,
    val timeTo: String
)

data class TimetableDayResponse(
    val date: String,
    val availableSlots: List<TimeSlot>
)