package pl.edu.ur.teachly.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_DISMISSED_TUTOR_IDS = stringPreferencesKey("dismissed_review_tutor_ids")
    }

    val dismissedTutorIdsFlow: Flow<Set<Int>> = context.dataStore.data
        .map { prefs ->
            val raw = prefs[KEY_DISMISSED_TUTOR_IDS] ?: ""
            if (raw.isBlank()) emptySet()
            else raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }

    suspend fun dismissTutors(tutorIds: Set<Int>) {
        if (tutorIds.isEmpty()) return
        context.dataStore.edit { prefs ->
            val existing = prefs[KEY_DISMISSED_TUTOR_IDS]
                ?.split(",")?.mapNotNull { it.toIntOrNull() }?.toMutableSet()
                ?: mutableSetOf()
            existing.addAll(tutorIds)
            prefs[KEY_DISMISSED_TUTOR_IDS] = existing.joinToString(",")
        }
    }
}
