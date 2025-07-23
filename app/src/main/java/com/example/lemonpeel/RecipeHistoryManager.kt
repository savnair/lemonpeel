package com.example.lemonpeel

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class for a recipe history entry
data class RecipeHistoryEntry(val name: String, val date: String)

// Extension property for DataStore
val Context.recipeHistoryDataStore by preferencesDataStore(name = "recipe_history")

object RecipeHistoryManager {
    private val RECIPES_KEY = stringSetPreferencesKey("recipes")
    private val DATE_SEPARATOR = "||" // To separate name and date in storage

    // Save a new recipe entry
    suspend fun addRecipe(context: Context, name: String) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val entryString = "$name$DATE_SEPARATOR$date"
        context.recipeHistoryDataStore.edit { prefs ->
            val current = prefs[RECIPES_KEY] ?: emptySet()
            prefs[RECIPES_KEY] = current + entryString
        }
    }

    // Get all recipe entries as a Flow
    fun getRecipeHistory(context: Context): Flow<List<RecipeHistoryEntry>> =
        context.recipeHistoryDataStore.data.map { prefs ->
            (prefs[RECIPES_KEY] ?: emptySet()).mapNotNull { entry ->
                val parts = entry.split(DATE_SEPARATOR)
                if (parts.size == 2) RecipeHistoryEntry(parts[0], parts[1]) else null
            }.sortedByDescending { it.date }
        }

    suspend fun clearHistory(context: Context) {
        context.recipeHistoryDataStore.edit { prefs ->
            prefs[RECIPES_KEY] = emptySet()
        }
    }

    // Delete a single recipe entry by name and date
    suspend fun deleteRecipe(context: Context, name: String, date: String) {
        val entryString = "$name$DATE_SEPARATOR$date"
        context.recipeHistoryDataStore.edit { prefs ->
            val current = prefs[RECIPES_KEY] ?: emptySet()
            prefs[RECIPES_KEY] = current - entryString
        }
    }
} 