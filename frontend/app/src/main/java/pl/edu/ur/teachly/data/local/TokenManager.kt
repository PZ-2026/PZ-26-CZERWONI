package pl.edu.ur.teachly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_ID = intPreferencesKey("user_id")
    }

    suspend fun saveAuthData(token: String, role: String, userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = role
            prefs[KEY_USER_ID] = userId
        }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_TOKEN] }

    val roleFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_ROLE] }

    val userIdFlow: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[KEY_USER_ID] }

    suspend fun clearAuthData() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_ROLE)
            prefs.remove(KEY_USER_ID)
        }
    }
}