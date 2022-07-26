package com.example.simmone

import android.content.Context
import com.example.simmone.dataStore.StorageManager
import com.example.simmone.dataStore.dataStore
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
        val instance = ProgressManager()
    }
}