package pl.edu.ur.teachly.data.repository

import pl.edu.ur.teachly.data.model.HolidayRequest
import pl.edu.ur.teachly.data.model.HolidayResponse
import pl.edu.ur.teachly.data.remote.HolidayApiService

class HolidayRepository(private val api: HolidayApiService) {

    suspend fun getAllHolidays(): Result<List<HolidayResponse>> {
        return try {
            val response = api.getAllHolidays()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd pobierania świąt"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun addHoliday(request: HolidayRequest): Result<HolidayResponse> {
        return try {
            val response = api.addHoliday(request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd dodawania święta"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun updateHoliday(id: Int, request: HolidayRequest): Result<HolidayResponse> {
        return try {
            val response = api.updateHoliday(id, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Błąd aktualizacji święta"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }

    suspend fun deleteHoliday(id: Int): Result<Unit> {
        return try {
            val response = api.deleteHoliday(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Błąd usuwania święta"))
        } catch (e: Exception) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        }
    }
}
