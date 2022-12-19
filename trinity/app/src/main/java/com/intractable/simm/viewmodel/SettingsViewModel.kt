package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config

class SettingsViewModel: ViewModel() {




    val settingsComment: LiveData<String>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSettingsComment().asLiveData()

        }

    var analyticsEnabled = false
    init {
        Config.instance.storageManager?.isAnalyticsEnabled!!.asLiveData().observeForever {
            analyticsEnabled = it
        }
    }

}