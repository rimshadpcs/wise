package com.example.simmone.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.*

class StorageManager(val dataStore: DataStore<Preferences>) {

    companion object {
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
        val PLANT_STATE_KEY = intPreferencesKey("PLANT_STATE")
        val PLANT_GROWTH_KEY = intPreferencesKey("PLANT_GROWTH")
    }

    suspend fun storeSessionNumber(sessionNumber: Int) {
        dataStore.edit {
            it[SESSION_COUNT_KEY] = sessionNumber
        }
    }

    suspend fun getSessionNumber(): Int? {
        return dataStore.data.firstOrNull()?.get(SESSION_COUNT_KEY)
    }

    suspend fun storePlantState(sessionNumber: Int) {
        dataStore.edit {
            it[PLANT_STATE_KEY] = sessionNumber
        }
    }
    suspend fun getPlantState(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_STATE_KEY)
    }

    suspend fun storePlantGrowth(sessionNumber: Int) {
        dataStore.edit {
            it[PLANT_GROWTH_KEY] = sessionNumber
        }
    }
    suspend fun getPlantGrowth(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_GROWTH_KEY)
    }

    // Flow for gold
    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }

}