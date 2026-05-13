package pl.edu.ur.teachly.data.repository

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.edu.ur.teachly.data.remote.ReportApiService
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ReportRepository(
    private val reportApiService: ReportApiService,
    private val context: Context,
) {
    suspend fun downloadReport(startDate: String, endDate: String): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                val response = reportApiService.getMyReport(startDate, endDate)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val fileName = "Raport_Teachly_${startDate}_${endDate}.pdf"
                        val downloadsDir =
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        if (downloadsDir != null && !downloadsDir.exists()) {
                            downloadsDir.mkdirs()
                        }
                        val file = File(downloadsDir, fileName)

                        var inputStream: InputStream? = null
                        var outputStream: FileOutputStream? = null

                        try {
                            inputStream = body.byteStream()
                            outputStream = FileOutputStream(file)
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                            outputStream.flush()
                            Result.success(file)
                        } catch (e: Exception) {
                            Result.failure(e)
                        } finally {
                            inputStream?.close()
                            outputStream?.close()
                        }
                    } else {
                        Result.failure(Exception("Brak danych pliku"))
                    }
                } else {
                    val errorBody =
                        response.errorBody()?.string() ?: "Błąd serwera: ${response.code()}"
                    Result.failure(Exception(errorBody))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
