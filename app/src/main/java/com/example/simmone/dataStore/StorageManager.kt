package com.example.simmone.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorageManager(val dataStore: DataStore<Preferences>) {

    companion object {
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
    }

    suspend fun storeSessionNumber(sessionNumber: Int) {
        dataStore.edit {
            it[SESSION_COUNT_KEY] = sessionNumber
        }
    }

    // Flow for gold
    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }

}