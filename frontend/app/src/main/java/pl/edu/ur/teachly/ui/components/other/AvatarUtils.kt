package pl.edu.ur.teachly.ui.components.other

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Wspólne funkcje pomocnicze dla obsługi zdjęć profilowych, używane zarówno na ekranie edycji
 * profilu, jak i w dialogu edycji użytkownika przez administratora.
 */

/**
 * Kopiuje obraz spod [uri] do tymczasowego pliku w katalogu cache aplikacji i zwraca ten plik
 * (lub {@code null} w razie błędu odczytu).
 */
internal fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("avatar_upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Sprawdza, czy podany URL wskazuje na rzeczywiste, własne zdjęcie profilowe użytkownika
 * (a nie pusty/placeholderowy adres). Decyduje m.in. o pokazaniu opcji usunięcia zdjęcia.
 */
internal fun hasCustomAvatar(avatarUrl: String?): Boolean = !avatarUrl.isNullOrBlank() &&
    !avatarUrl.equals("null", ignoreCase = true) &&
    !avatarUrl.contains("/null", ignoreCase = true) &&
    !avatarUrl.endsWith("/uploads/avatars/", ignoreCase = true) &&
    avatarUrl.contains("/")
