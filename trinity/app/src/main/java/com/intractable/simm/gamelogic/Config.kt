package com.intractable.simm.gamelogic

import android.content.Context
import android.util.Log
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.dataStore.dataStore

class Config() {

    val progressManager: IProgressManager
    var storageManager: StorageManager?
    var applicationContext: Context?

    init {
        Log.i("Config", "Setting up config objects")
        progressManager = ProgressManagerImpl.instance
        storageManager = null
        applicationContext = null
    }

    fun initStorageManager(context: Context) {
        applicationContext = context
        storageManager = StorageManager(context.dataStore)
        progressManager.initStorageManager(storageManager!!)
    }

    suspend fun getInitializedStorageManager() : StorageManager? {
        return (progressManager as ProgressManagerImpl).getInitializedStorageManager()
    }

    companion object {
        val instance = Config()
    }

}