package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.quizDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "buddhism_quiz_progress"
)

class QuizProgressStore(private val context: Context) {
    private val answersKey = stringPreferencesKey("answers_encoded")

    val savedAnswers: Flow<Map<Int, Int>> = context.quizDataStore.data.map { prefs ->
        decodeAnswers(prefs[answersKey].orEmpty())
    }

    suspend fun saveAnswers(answers: Map<Int, Int>) {
        context.quizDataStore.edit { prefs ->
            if (answers.isEmpty()) {
                prefs.remove(answersKey)
            } else {
                prefs[answersKey] = encodeAnswers(answers)
            }
        }
    }

    suspend fun clearAnswers() {
        context.quizDataStore.edit { prefs ->
            prefs.remove(answersKey)
        }
    }

    private fun encodeAnswers(answers: Map<Int, Int>): String =
        answers.entries
            .sortedBy { it.key }
            .joinToString(",") { "${it.key}:${it.value}" }

    private fun decodeAnswers(raw: String): Map<Int, Int> {
        if (raw.isBlank()) return emptyMap()
        return buildMap {
            raw.split(",").forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val qIdx = parts[0].toIntOrNull()
                    val optIdx = parts[1].toIntOrNull()
                    if (qIdx != null && optIdx != null && qIdx in 0..49 && optIdx in 0..3) {
                        put(qIdx, optIdx)
                    }
                }
            }
        }
    }
}
