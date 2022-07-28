package com.example.simmone.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.*

class StorageManager(val dataStore: DataStore<Preferences>) {

    companion object {
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
        val PLANT_STATE_KEY = intPreferencesKey("PLANT_STATE")  // 0 == awake, 1 == asleep, 2 == surprised
        val PLANT_GROWTH_KEY = intPreferencesKey("PLANT_GROWTH")  // 0-5 for tulips
        val PLANT_TYPE_KEY = intPreferencesKey("PLANT_TYPE")  // 0 == purple, 1 == red, 2 == yellow
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
    suspend fun incrementPlantGrowth(){
        val key = PLANT_GROWTH_KEY
        dataStore.edit{
            val curState = dataStore.data.firstOrNull()?.get(key)
            if(curState != null) {
                if(curState == 5)  // currently only have 6 plant growth stages should index should only go up to 5
                    it[key] = 0
                else
                    it[key] = curState + 1
            }
        }
    }
    suspend fun getPlantGrowth(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_GROWTH_KEY)
    }

    suspend fun storePlantType(sessionNumber: Int) {
        dataStore.edit {
            it[PLANT_TYPE_KEY] = sessionNumber
        }
    }
    suspend fun incrementPlantType(){
        val key = PLANT_TYPE_KEY
        dataStore.edit{
            val curState = dataStore.data.firstOrNull()?.get(key)
            if(curState != null) {
                if(curState == 2)  // currently only have 3 plant types so index should only go up to 2
                    it[key] = 0
                else
                    it[key] = curState + 1
            }
        }
    }
    suspend fun getPlantType(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_TYPE_KEY)
    }

    // Flow for gold
    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }

}