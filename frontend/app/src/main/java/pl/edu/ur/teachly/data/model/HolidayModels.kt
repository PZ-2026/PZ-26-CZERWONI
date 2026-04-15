package pl.edu.ur.teachly.data.model

data class HolidayRequest(
    val holidayDate: String,
    val description: String?
)

data class HolidayResponse(
    val id: Int,
    val holidayDate: String,
    val description: String?
)