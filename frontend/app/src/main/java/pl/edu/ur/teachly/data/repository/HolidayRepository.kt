package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.HolidayRequest
import pl.edu.ur.teachly.data.model.HolidayResponse
import pl.edu.ur.teachly.data.remote.HolidayApiService
import pl.edu.ur.teachly.data.remote.apiCall
import pl.edu.ur.teachly.data.remote.apiCallUnit

class HolidayRepository(private val api: HolidayApiService) {

    suspend fun getAllHolidays(): Result<List<HolidayResponse>> =
        apiCall("Błąd pobierania świąt") { api.getAllHolidays() }

    suspend fun addHoliday(request: HolidayRequest): Result<HolidayResponse> =
        apiCall("Błąd dodawania święta") { api.addHoliday(request) }

    suspend fun updateHoliday(id: Int, request: HolidayRequest): Result<HolidayResponse> =
        apiCall("Błąd aktualizacji święta") { api.updateHoliday(id, request) }

    suspend fun deleteHoliday(id: Int): Result<Unit> = apiCallUnit("Błąd usuwania święta") { api.deleteHoliday(id) }
}
