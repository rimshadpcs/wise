package com.example.simmone.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simmone.model.QuestionItem
import com.example.simmone.model.SessionModel
import com.example.simmone.utils.Constants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class SessionViewModel:ViewModel() {


    //FragmentA
    private var questionItems: ArrayList<QuestionItem> = ArrayList()
    private var ListActivity: ArrayList<SessionModel> = ArrayList()
    val mcqData = MutableLiveData<ArrayList<QuestionItem>>()
    val ListActivityData = MutableLiveData<ArrayList<SessionModel>>()
    fun getListActivity():LiveData<ArrayList<SessionModel>> = ListActivityData
    fun getMcQData(): LiveData<ArrayList<QuestionItem>> = mcqData
    var quizFile = MutableLiveData<String>()
    val eventlivedata = MutableLiveData(Constants.EVENT_NONE)
    var current_question = 0;
    var fragmentLiveData = MutableLiveData<String>("")
    var session_num = 0
    var page = 0

    fun getFragment():LiveData<String> = fragmentLiveData

    fun getEvent(): LiveData<Int?> = eventlivedata


    fun loadAllQuestions(context: Context) {
        questionItems.clear()
        val jsonStr = loadJSONFromAsset(context)
        Log.e("json",jsonStr)

        try {
            val jsonObject = JSONObject(jsonStr)
            var fragment = jsonObject.getString("fragmentClassName")
            if (fragment.equals("FragmentMcq")) {
                val questions = jsonObject.getJSONObject("parameters")
                val questionString = questions.getString("question")
                val choice1String = questions.getString("choice1")
                val choice2String = questions.getString("choice2")
                val choice3String = questions.getString("choice3")
                val choice4String = questions.getString("choice4")
                val answer = questions.getString("answer")
                questionItems.add(
                    QuestionItem(
                        questionString,
                        choice1String,
                        choice2String,
                        choice3String,
                        choice4String,
                        answer
                    )
                )
                mcqData.value = questionItems
            }

            fragmentLiveData.value = fragment
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun loadJSONFromAsset(context: Context): String {
        Log.e("Quizfile",quizFile.value!!)
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            if(quizFile.value != null){
                val `is` = context.assets.open(quizFile.value!!+".json")
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                json = String(buffer, charset)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

    fun loadSession(context: Context) {
        val jsonStr = loadSessionFromAsset(context)

        questionItems = ArrayList()
        try {
            val jsonObject = JSONObject(jsonStr)
            val sessionList = jsonObject.getJSONArray("sessionList")
            for (i in 0 until sessionList.length()) {
                val question = sessionList.getJSONObject(i)
                val sessionId = question.getString("sessionId")
                Log.e("sessionId",sessionId)
                val activityList = question.getJSONArray("activityList")
                var sessionModel = SessionModel()
                sessionModel.sessionId = sessionId
                sessionModel.activityList = activityList.toArrayList()
                ListActivity.add(sessionModel)
            }
            ListActivityData.value = ListActivity
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
        Log.e("Json:",json)
        return json
    }
    public fun checkForNextQuestion(){
        // load next question if any
        if (page < ListActivity[0].activityList!!.size-1) {
            Log.e("next","sd")
            page++
            eventlivedata.value = Constants.EVENT_NEXT_PAGE
        }
        else{
            eventlivedata.value = Constants.EVENT_FINISH_SESSION
        }
    }

    fun JSONArray.toArrayList(): ArrayList<String> {
        val list = arrayListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }

        return list
    }
}