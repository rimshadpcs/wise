package com.intractable.simm.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gold_count")
class Gold {
    //val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gold_count")
}