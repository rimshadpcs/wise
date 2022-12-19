package com.intractable.simm.dataStore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.math.MathUtils
import com.intractable.simm.model.PlantItem
import com.intractable.simm.utils.DateTimeFunctions
import com.intractable.simm.utils.Plants
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "simm_data")
class StorageManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val USER_INPUT_NAME_KEY = stringPreferencesKey("USER_INPUT_NAME")
        val SESSION_COUNT_KEY = intPreferencesKey("SESSION_COUNT")
        val SESSION_STATUS_KEY = stringPreferencesKey("SESSION_STATUS")
        val SESSIONS_COMPLETED_KEY = intPreferencesKey("SESSIONS_COMPLETED")
        val PLANT_STATE_KEY = intPreferencesKey("PLANT_STATE")  // 0 == awake, 1 == asleep, 2 == surprised
        val PLANT_GROWTH_KEY = intPreferencesKey("PLANT_GROWTH")  // 0-4 for tulips
        val PLANT_TYPE_KEY = intPreferencesKey("PLANT_TYPE")  // 0 == purple, 1 == red, 2 == yellow
        val PLANTS_COLLECTED_COUNT = intPreferencesKey("PLANTS_COLLECTED_COUNT")  // how many plants you have collected
        val PLANT_IMAGE_KEY = intPreferencesKey("PLANT_IMAGE")
        val PLANTS_COLLECTED_NAMES = stringPreferencesKey("PLANTS_COLLECTED_NAMES")  // names of plants collected
        val SESSION_TIME_KEY = stringPreferencesKey("SESSION_TIME")
        val SESSION_TIME_EPOCH_KEY = stringPreferencesKey("SESSION_TIME_EPOCH")
        val DAILY_NOTIFICATION_KEY = intPreferencesKey("DAILY_NOTIFICATION")
        val HEARTS_KEY = intPreferencesKey("HEARTS")
        val HEARTS_REGEN_KEY = stringPreferencesKey("HEARTS_REGEN")
        val WIDGET_REVEAL_KEY = booleanPreferencesKey("WIDGET_REVEAL")
        val CAMERA_PERMISSION_DENIED = booleanPreferencesKey("CAMERA_PERMISSION_DENIED")
        val ANALYTICS_ENABLED = booleanPreferencesKey("ANALYTICS_ENABLED")
        val PLANT_GROWTH_ADJUSTED = intPreferencesKey("PLANT_GROWTH_ADJUSTED")
        val PLANT_GROWTH_MAX_ADJUSTED = intPreferencesKey("PLANT_GROWTH_MAX_ADJUSTED")
        val APP_VERSION_KEY = intPreferencesKey("APP_VERSION")
        val LAST_SESSION_TIMESTAMP_KEY = longPreferencesKey("LAST_SESSION_TIMESTAMP")
        val GAME_HIGHSCORE_KEY = stringPreferencesKey("GAME_HIGHSCORE")
    }
    var gameHighscores: MutableMap<String, String> = mutableMapOf()


    suspend fun storeAnalyticsStatus(permission: Boolean) {
        dataStore.edit {
            it[ANALYTICS_ENABLED] = permission
        }
    }

    var isAnalyticsEnabled:Flow<Boolean>? = dataStore.data.map {
        it[ANALYTICS_ENABLED]?:true
    }

    suspend fun storePermissionStatus(permission: Boolean) {
        dataStore.edit {
            it[CAMERA_PERMISSION_DENIED] = permission
        }
    }

    var cameraPermissionDenied:Flow<Boolean>? = dataStore.data.map {
        it[CAMERA_PERMISSION_DENIED]?:false
    }

    suspend fun storeSessionNumber(sessionNumber: Int) {
        dataStore.edit {
            it[SESSION_COUNT_KEY] = sessionNumber
        }
        calculatePlant()
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

    suspend fun getPlantGrowth(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_GROWTH_KEY)
    }

    suspend fun storePlantType(sessionNumber: Int) {
        dataStore.edit {
            it[PLANT_TYPE_KEY] = sessionNumber
        }
    }
    suspend fun getPlantType(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_TYPE_KEY)
    }
    val plantTypeFlow: Flow<Int?> = dataStore.data.map {
        it[PLANT_TYPE_KEY]
    }

    suspend fun storePlantCount(plantCount: Int) {
        dataStore.edit {
            it[PLANTS_COLLECTED_COUNT] = plantCount
        }
    }
    suspend fun getPlantCount(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANTS_COLLECTED_COUNT)
    }

    private suspend fun calculatePlant() {
        val plantState = 0  // keep at 0
        var sessionsCompleted = getSessionsCompleted()
        var plantGrowthStage = 0
        var plantType = 0
        var plantsCollectedInt = 0

        val plantGrowthIntervals = Plants.plantGrowthIntervals

        while(sessionsCompleted > plantGrowthIntervals[kotlin.math.min(plantsCollectedInt, plantGrowthIntervals.size - 1)]){
            sessionsCompleted -= plantGrowthIntervals[kotlin.math.min(plantsCollectedInt, plantGrowthIntervals.size - 1)]
            plantsCollectedInt++
        }

        // set plant type
        plantType = plantsCollectedInt

        // store adjusted values
        storePlantGrowthAdjusted(sessionsCompleted)
        storePlantGrowthMaxAdjusted(Plants.plantGrowthIntervals[kotlin.math.min(plantsCollectedInt, plantGrowthIntervals.size - 1)])

        // find plant growth stage
        val plantStageFloat = MathUtils.lerp(
            0f,
            4f,
            sessionsCompleted.toFloat() / Plants.plantGrowthIntervals[kotlin.math.min(plantsCollectedInt, plantGrowthIntervals.size - 1)].toFloat()
        )
        plantGrowthStage = plantStageFloat.roundToInt()

        // if exactly full plant, collected count ++
        if(sessionsCompleted == plantGrowthIntervals[kotlin.math.min(plantsCollectedInt, plantGrowthIntervals.size - 1)]){
            plantsCollectedInt++
        }

        // store plant item collection
        val plantdexCollection: ArrayList<PlantItem> = ArrayList()
        for(i in 0 until plantsCollectedInt){
            plantdexCollection.add(PlantItem(Plants.dexIcons[kotlin.math.min(i % Plants.plantImages.size, Plants.dexIcons.size - 1)]))
        }

        // to avoid out of index errors
        plantType %= Plants.plantImages.size

        Log.d("storageManager",
            "PlantsCollected: $plantsCollectedInt plantGrowthStage: $plantGrowthStage"
        )

        storePlantGrowth(plantGrowthStage)
        storePlantType(plantType)
        storePlantCount(plantsCollectedInt)
        storePlantCollection(plantdexCollection)
        storePlantImage(Plants.plantImages[plantType][plantState][kotlin.math.min(plantGrowthStage + 1, Plants.plantImages[plantType][plantState].size - 1)])
    }

    val plantImageFlow: Flow<Int?> = dataStore.data.map {
        it[PLANT_IMAGE_KEY]
    }

    val plantCountFlow: Flow<Int?> = dataStore.data.map {
        it[PLANTS_COLLECTED_COUNT]
    }

    suspend fun storePlantCollection(plantsCollected: ArrayList<PlantItem>) {
        var storageString: String = ""
        for(plantItem: PlantItem in plantsCollected){
            storageString += plantItem.plantImage.toString() + "&"
        }
        dataStore.edit {
            it[PLANTS_COLLECTED_NAMES] = storageString
        }
    }
    suspend fun getPlantCollection(): ArrayList<PlantItem> {
        val storageString = dataStore.data.firstOrNull()?.get(PLANTS_COLLECTED_NAMES)?:""
        val splitPlantsCollection = storageString.split("&")
        val plantCollectionItems: ArrayList<PlantItem> = ArrayList()
        for (plant: String in splitPlantsCollection){
            if (plant.isNotBlank()) {
                val newPlantItem: PlantItem = PlantItem(plant.toInt())
                plantCollectionItems.add(newPlantItem)
            }
        }
        return plantCollectionItems
    }
    val getPlantCollectionFlow: Flow<String?> = dataStore.data.map {
        it[PLANTS_COLLECTED_NAMES]
    }

    val sessionCountFlow: Flow<Int?> = dataStore.data.map {
        it[SESSION_COUNT_KEY]
    }


    suspend fun storeSessionTime(sessionTime: String) {
        dataStore.edit {
            it[SESSION_TIME_KEY] = sessionTime
        }
    }
    suspend fun getSessionTime(): String? {
        return dataStore.data.firstOrNull()?.get(SESSION_TIME_KEY)
    }
    val sessionTimeFlow: Flow<String?> = dataStore.data.map {
        it[SESSION_TIME_KEY]
    }

    suspend fun storeSessionTimeEpoch(sessionTime: String) {
        dataStore.edit {
            it[SESSION_TIME_EPOCH_KEY] = sessionTime
        }
    }
    suspend fun getSessionTimeEpoch(): String? {
        return dataStore.data.firstOrNull()?.get(SESSION_TIME_EPOCH_KEY)
    }
    val sessionTimeEpochFlow: Flow<String?> = dataStore.data.map {
        it[SESSION_TIME_EPOCH_KEY]
    }

    suspend fun storeDailyNotification(input: Int) {
        dataStore.edit {
            it[DAILY_NOTIFICATION_KEY] = input
        }
    }
    suspend fun getDailyNotification(): Int? {
        return dataStore.data.firstOrNull()?.get(DAILY_NOTIFICATION_KEY)
    }
    val dailyNotificationFlow: Flow<Int?> = dataStore.data.map {
        it[DAILY_NOTIFICATION_KEY]
    }

    // I'm thinking when they lose a heart, they need to get the hearts,
    // then minus one heart. If leftover hearts <= 0, they fail the session
    // then store the hearts back into here
    suspend fun storeHearts(input: Int) {
        if(input == 4){
            storeHeartsRegen(DateTimeFunctions.getCurrentDateTimeEpochString())
        }
        dataStore.edit {
            it[HEARTS_KEY] = input
        }
    }
    suspend fun getHearts(): Int? {
        return dataStore.data.firstOrNull()?.get(HEARTS_KEY)
    }
    val heartsFlow: Flow<Int?> = dataStore.data.map {
        it[HEARTS_KEY]
    }

    suspend fun storeHeartsRegen(input: String) {
        dataStore.edit {
            it[HEARTS_REGEN_KEY] = input
        }
    }
    suspend fun getHeartsRegen(): String? {
        return dataStore.data.firstOrNull()?.get(HEARTS_REGEN_KEY)
    }

    suspend fun storeWidgetReveal(hasWidgetReveal: Boolean) {
        dataStore.edit {
            it[WIDGET_REVEAL_KEY] = hasWidgetReveal
        }
    }

    suspend fun getWidgetReveal(): Boolean? {
        return dataStore.data.firstOrNull()?.get(WIDGET_REVEAL_KEY)
    }

    val heartsRegenFlow: Flow<String?> = dataStore.data.map {
        it[HEARTS_REGEN_KEY]
    }


    suspend fun storePlantGrowthAdjusted(input: Int) {
        dataStore.edit {
            it[PLANT_GROWTH_ADJUSTED] = input
        }
    }
    suspend fun getPlantGrowthAdjusted(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_GROWTH_ADJUSTED)
    }


    suspend fun storePlantGrowthMaxAdjusted(input: Int) {
        dataStore.edit {
            it[PLANT_GROWTH_MAX_ADJUSTED] = input
        }
    }
    suspend fun getPlantGrowthMaxAdjusted(): Int? {
        return dataStore.data.firstOrNull()?.get(PLANT_GROWTH_MAX_ADJUSTED)
    }

    suspend fun storeLastSessionTimestamp(input: Long) {
        dataStore.edit {
            it[LAST_SESSION_TIMESTAMP_KEY] = input
        }
    }

    suspend fun getLastSessionTimestamp(): Long? {
        return dataStore.data.firstOrNull()?.get(LAST_SESSION_TIMESTAMP_KEY)
    }

    enum class SessionStatus{
        NotDone, Done, AttemptedIncomplete
    }
    suspend fun storeSessionStatus(sessionNumber: Int, status: SessionStatus){
        // Statuses: 0 == not done, 1 == done, 2 == attempted but incomplete
        Log.d("sessionStatus String", "sessionNumber: $sessionNumber, inputStatus: $status")
        val newString: String
        newString = when(status){
            SessionStatus.NotDone -> "0"
            SessionStatus.Done -> "1"
            SessionStatus.AttemptedIncomplete -> "2"
            else -> "1"
        }
        var sessionsString = getSessionStatus()?:""
        val sessionsEnteredLength = sessionsString.length
        Log.d("sessionStatus String", "Before: $sessionsString")

        // Populate sessions with completed if sessionsString is empty and sessionCount is not 0
        if((getSessionNumber() ?: 0) > 0 && sessionsString == ""){
            for(i in sessionsEnteredLength until sessionNumber){
                sessionsString += "1"
            }
        }

        // if this is a new session done
        if(sessionNumber >= sessionsEnteredLength){
            // in case somehow not attempted sessions were skipped
            for(i in sessionsEnteredLength until sessionNumber){
                sessionsString += "0"
            }
            sessionsString += newString

            dataStore.edit{
                it[SESSION_STATUS_KEY] = sessionsString
            }
        }
        else{
            val currentStatus = sessionsString[sessionNumber]
            if(currentStatus.compareTo('1') == 0){  // if the session is already marked as Done, do nothing

            }
            else{  // if the session is NotDone or AttemptedIncomplete, update the sessionString
                sessionsString = StringBuilder(sessionsString).also{it.setCharAt(sessionNumber, newString.first())}.toString()

                dataStore.edit{
                    it[SESSION_STATUS_KEY] = sessionsString
                }
            }
        }
        Log.d("sessionStatus String", "After: $sessionsString")

        calculateAndStoreSessionsCompleted(sessionsString)
        storeSessionNumber(sessionsString.length)
    }
    suspend fun getSessionStatus(): String? {
        return dataStore.data.firstOrNull()?.get(SESSION_STATUS_KEY)
    }
    suspend fun getSessionStatusAt(index: Int): String{
        val wholeString = dataStore.data.firstOrNull()?.get(SESSION_STATUS_KEY)?:""
        if(index >= wholeString.length){
            return ""
        }
        else{
            return wholeString[index].toString()
        }
    }

    suspend fun calculateAndStoreSessionsCompleted(sessionsString: String){
        var counter = 0
        for(i in sessionsString.indices){
            if(sessionsString[i] == '1'){
                counter++
            }
        }

        dataStore.edit{
            it[SESSIONS_COMPLETED_KEY] = counter
        }
    }
    suspend fun getSessionsCompleted(): Int{
        return dataStore.data.firstOrNull()?.get(SESSIONS_COMPLETED_KEY)?:0
    }


    suspend fun storeUserInputName(input: String) {
        dataStore.edit {
            it[USER_INPUT_NAME_KEY] = input
        }
    }
    suspend fun getUserInputName(): String? {
        return dataStore.data.firstOrNull()?.get(USER_INPUT_NAME_KEY)
    }



    suspend fun storeAppVersion(input: Int){
        dataStore.edit{
            it[APP_VERSION_KEY] = input
        }
    }
    suspend fun checkAppVersion() : Boolean{
        /**
         * Version 0: Release 2.5, 30/9/2022
         * Version 1: Release 2.6, 1/11/2022
         */
        val newestVersionNumber = 1  // update this for every new version
        var storedVersion = dataStore.data.firstOrNull()?.get(APP_VERSION_KEY) ?:0
        var hasVersionChanged = false
        val curSessionNumber = getSessionNumber()?:0

        if(storedVersion == newestVersionNumber){
            return false
        }
        if(curSessionNumber == 0){  // if no sessions have been done, do nothing
            storeAppVersion(newestVersionNumber)
            return false
        }

        if(storedVersion == 0){  // if version is release 2.5
            var sessionStatusString = ""
            for(i in 0 until curSessionNumber){
                sessionStatusString += "1"
            }
            dataStore.edit{
                it[SESSION_STATUS_KEY] = sessionStatusString
            }

            dataStore.edit{
                it[SESSIONS_COMPLETED_KEY] = curSessionNumber - 1
            }

            calculatePlant()  // recalculate plant values

            storedVersion = 1
            hasVersionChanged = true
        }
//        if(storedVersion == 1){  // for the future, in release 2.7
//
//            storedVersion = 2
//            hasVersionChanged = true
//        }

        storeAppVersion(storedVersion)

        return hasVersionChanged
    }
}