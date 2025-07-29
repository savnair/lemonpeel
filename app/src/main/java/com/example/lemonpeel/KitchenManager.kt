package com.example.lemonpeel

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
val Context.kitchenDataStore by preferencesDataStore(name = "kitchen_items")

data class KitchenItem(
    val id: String,
    val name: String,
    val quantity: String,
    val category: KitchenCategory
)

enum class KitchenCategory {
    INGREDIENTS, TOOLS, COOKWARE
}

object KitchenManager {
    private val KITCHEN_ITEMS_KEY = stringSetPreferencesKey("kitchen_items")
    private val SEPARATOR = "|||" // Separator for storing item data

    // Save a kitchen item
    suspend fun saveKitchenItem(context: Context, item: KitchenItem) {
        val itemString = "${item.id}$SEPARATOR${item.name}$SEPARATOR${item.quantity}$SEPARATOR${item.category}"
        context.kitchenDataStore.edit { prefs ->
            val current = prefs[KITCHEN_ITEMS_KEY] ?: emptySet()
            // Remove existing item with same ID if it exists
            val filtered = current.filterNot { existing ->
                try {
                    val parts = existing.split(SEPARATOR)
                    if (parts.size >= 4) {
                        parts[0] == item.id // Compare by ID
                    } else false
                } catch (e: Exception) {
                    false
                }
            }.toSet()
            prefs[KITCHEN_ITEMS_KEY] = filtered + itemString
        }
    }

    // Get all kitchen items as a Flow
    fun getKitchenItems(context: Context): Flow<List<KitchenItem>> =
        context.kitchenDataStore.data.map { prefs ->
            (prefs[KITCHEN_ITEMS_KEY] ?: emptySet()).mapNotNull { itemString ->
                try {
                    val parts = itemString.split(SEPARATOR)
                    if (parts.size >= 4) {
                        KitchenItem(
                            id = parts[0],
                            name = parts[1],
                            quantity = parts[2],
                            category = KitchenCategory.valueOf(parts[3])
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        }

    // Delete a kitchen item
    suspend fun deleteKitchenItem(context: Context, itemId: String) {
        context.kitchenDataStore.edit { prefs ->
            val current = prefs[KITCHEN_ITEMS_KEY] ?: emptySet()
            val filtered = current.filterNot { itemString ->
                try {
                    val parts = itemString.split(SEPARATOR)
                    if (parts.size >= 4) {
                        parts[0] == itemId // Compare by ID
                    } else false
                } catch (e: Exception) {
                    false
                }
            }.toSet()
            prefs[KITCHEN_ITEMS_KEY] = filtered
        }
    }

    // Clear all kitchen items
    suspend fun clearAllItems(context: Context) {
        context.kitchenDataStore.edit { prefs ->
            prefs[KITCHEN_ITEMS_KEY] = emptySet()
        }
    }
} 