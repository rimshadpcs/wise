package com.example.simmone.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoldManager(val dataStore: DataStore<Preferences>) {

    companion object {
        val GOLD_COUNT_KEY = intPreferencesKey("GOLD_COUNT")
    }

    suspend fun storeGold() {
        dataStore.edit {
            it[GOLD_COUNT_KEY] = (it[GOLD_COUNT_KEY] ?: 0) + 1
        }
    }

    // Flow for gold
    val goldCountFlow: Flow<Int?> = dataStore.data.map {
        it[GOLD_COUNT_KEY]
    }

}