package com.intractable.simm

import android.content.Context
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.dataStore.dataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProgressManager {
    var sessionNumber = 0

    fun saveProgress(context: Context) {
        sessionNumber++
        val storageManager = StorageManager(context.dataStore)
        GlobalScope.launch {
            storageManager.storeSessionNumber(sessionNumber)
        }
    }

    companion object {
        val instance = com.intractable.simm.ProgressManager()
    }
}