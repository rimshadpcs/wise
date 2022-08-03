package com.intractable.simm.dataStore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.android.material.math.MathUtils
import com.intractable.simm.utils.Plants
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

class StorageManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
        val PLANT_STATE_KEY = intPreferencesKey("PLANT_STATE")  // 0 == awake, 1 == asleep, 2 == surprised
        val PLANT_GROWTH_KEY = intPreferencesKey("PLANT_GROWTH")  // 0-5 for tulips
        val PLANT_TYPE_KEY = intPreferencesKey("PLANT_TYPE")  // 0 == purple, 1 == red, 2 == yellow
        val PLANTS_COLLECTED_COUNT = intPreferencesKey("PLANTS_COLLECTED_COUNT")
        val PLANT_IMAGE_KEY = intPreferencesKey("PLANT_IMAGE")
    }

    suspend fun storeSessionNumber(sessionNumber: Int) {
        dataStore.edit {
            it[SESSION_COUNT_KEY] = sessionNumber
        }
        calculatePlant(sessionNumber)
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

    suspend fun storePlantImage(plantImageVal: Int) {
        dataStore.edit {
            it[PLANT_IMAGE_KEY] = plantImageVal
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

    suspend fun storePlantCount(plantCount: Int) {
        dataStore.edit {
            it[PLANTS_COLLECTED_COUNT] = plantCount
        }
    }
    suspend fun getPlantCount(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANTS_COLLECTED_COUNT)
    }

    private suspend fun calculatePlant(initialSessionsCompleted: Int) {
        var plantGrowthIndex = 0
        var numberOfPlantsCollected = 0
        var sessionsCompleted = initialSessionsCompleted
        var plantType = 1 // red plant
        var plantState = 0

        Log.d("StorageManager","plant_type = " + plantType)
        Log.d("StorageManager","plant_state = " + plantState)
        Log.d("StorageManager","plant_sessionsCompleted = " + sessionsCompleted)

        // Converts sessionsCompleted into a plant growth size depending on sessions done
        // Basically a % but with variable divisor
        while (sessionsCompleted > Plants.plantGrowthIntervals[plantGrowthIndex]) {
            sessionsCompleted -= Plants.plantGrowthIntervals[plantGrowthIndex]
            plantGrowthIndex++
            numberOfPlantsCollected++
            if (plantGrowthIndex >= Plants.plantGrowthIntervals.size) {  // don't go out of index bounds
                plantGrowthIndex = Plants.plantGrowthIntervals.size - 1
            }
        }

        // normalizes growth progress to maximum growth allowed in this interval
        val plantStageFloat = MathUtils.lerp(
            0f,
            5f,
            sessionsCompleted.toFloat() / Plants.plantGrowthIntervals[plantGrowthIndex]
        )
        // turn the number back into int
        sessionsCompleted = plantStageFloat.roundToInt()
        Log.d("StorageManager", "plant growth = " + sessionsCompleted)

        val plantImage = Plants.plantImages[plantType][plantState][sessionsCompleted]

        storePlantGrowth(sessionsCompleted)
        storePlantCount(numberOfPlantsCollected)
        storePlantImage(plantImage)
        Log.i("StorageManager", "plantCount = " + numberOfPlantsCollected)
        Log.i("StorageManager", "plantImage = " + plantImage)
    }

    val plantImageFlow: Flow<Int?> = dataStore.data.map {
        it[PLANT_IMAGE_KEY]
    }

    val plantCountFlow: Flow<Int?> = dataStore.data.map {
        it[PLANTS_COLLECTED_COUNT]
    }

    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }

}