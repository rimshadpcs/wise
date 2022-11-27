package com.intractable.simm.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.intractable.simm.gamelogic.Config
import kotlinx.coroutines.flow.Flow
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MainViewModel() : ViewModel() {

    val activityList = ArrayList<String>()
    var sessionNumLiveData  =MutableLiveData(0)
    fun getSessionNum():LiveData<Int> = sessionNumLiveData

    val plantImageFlow: LiveData<Int>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantImageFlow().asLiveData()
        }

    val plantCountFlow: LiveData<Int>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantCountFlow().asLiveData()
        }

    fun loadSession(context: Context) {
        Log.i("SessionActivity", "Loading session description from json")
        val jsonStr = loadSessionFromAsset(context)

        try {
            val jsonObject = JSONObject(jsonStr)
            val sessionList = jsonObject.getJSONArray("sessionList")
            for (i in 0 until sessionList.length()) {
                val para = sessionList.getJSONObject(i)
                val title = para.getString("title")
                activityList.add(title)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadSessionFromAsset(context: Context): String {
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            val `is` = context.assets.open("session.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.e("Json:", json)
        return json
    }

    fun getSessionModelLiveData(): Any {
         val sessionModelFlow = Config.instance.progressManager.getSessionModelFlow()
         return (sessionModelFlow as Flow<*>).asLiveData()
     }

}


