package pl.edu.ur.teachly.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import pl.edu.ur.teachly.data.local.SessionManager
import pl.edu.ur.teachly.data.local.TokenManager

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getCachedToken()
        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401 && !token.isNullOrBlank()) {
            scope.launch {
                tokenManager.clearAuthData()
                sessionManager.notifySessionExpired()
            }
        }

        return response
    }
}
