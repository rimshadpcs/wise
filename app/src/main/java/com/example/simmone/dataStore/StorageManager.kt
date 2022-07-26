package com.example.simmone.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.*

class StorageManager(val dataStore: DataStore<Preferences>) {

    companion object {
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
    }

    suspend fun storeSessionNumber(sessionNumber: Int) {
        dataStore.edit {
            it[SESSION_COUNT_KEY] = sessionNumber
        }
    }

    suspend fun getSessionNumber(): Int? {
        return dataStore.data.firstOrNull()?.get(SESSION_COUNT_KEY)
    }

    // Flow for gold
    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }

}