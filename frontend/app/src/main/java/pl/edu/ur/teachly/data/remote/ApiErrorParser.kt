package pl.edu.ur.teachly.data.remote

import org.json.JSONObject
import retrofit2.Response

object ApiErrorParser {

    fun parseDetail(response: Response<*>): String? = try {
        response.errorBody()?.string()?.let { body ->
            JSONObject(body).optString("detail").takeIf { it.isNotBlank() }
        }
    } catch (_: Exception) {
        null
    }

    fun toException(response: Response<*>, fallback: String): Exception = Exception(parseDetail(response) ?: fallback)
}

fun <T> Response<T>.toResult(fallback: String): Result<T> = if (isSuccessful) {
    Result.success(body()!!)
} else {
    Result.failure(ApiErrorParser.toException(this, fallback))
}

inline fun <T> apiCall(
    fallback: String,
    networkError: String = "Brak połączenia z serwerem",
    block: () -> Response<T>
): Result<T> = try {
    block().toResult(fallback)
} catch (_: Exception) {
    Result.failure(Exception(networkError))
}

inline fun apiCallUnit(
    fallback: String,
    networkError: String = "Brak połączenia z serwerem",
    block: () -> Response<Unit>
): Result<Unit> = try {
    val response = block()
    if (response.isSuccessful) {
        Result.success(Unit)
    } else {
        Result.failure(ApiErrorParser.toException(response, fallback))
    }
} catch (_: Exception) {
    Result.failure(Exception(networkError))
}
