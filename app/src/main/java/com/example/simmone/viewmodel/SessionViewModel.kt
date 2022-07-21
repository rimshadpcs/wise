package com.example.simmone.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simmone.model.*
import com.example.simmone.utils.Constants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class SessionViewModel:ViewModel() {


    //FragmentMcq
    private var questionItems: ArrayList<QuestionItem> = ArrayList()
    private var ListActivity: ArrayList<SessionModel> = ArrayList()
    val mcqData = MutableLiveData<ArrayList<QuestionItem>>()
    val ListActivityData = MutableLiveData<ArrayList<SessionModel>>()
    fun getListActivity():LiveData<ArrayList<SessionModel>> = ListActivityData
    fun getMcQData(): LiveData<ArrayList<QuestionItem>> = mcqData
    var quizFile = MutableLiveData<String>()
    var current_question = 0;
    var fragmentLiveData = MutableLiveData<String>("")
    var session_num = 0
    var page = 0

    //FragmentTrueOrFalse
    var answer = ""
    var newMStatementList = MutableLiveData<ArrayList<Statement>>()
    val ToFData = MutableLiveData<TrueOrFalseModel>()
    val ToFlist = MutableLiveData<ArrayList<Statement>>()
    fun getToFList(): LiveData<ArrayList<Statement>> = ToFlist

    //FragmentOperation
    var notItems: ArrayList<String> = ArrayList()
    var notTxtLivedata:MutableLiveData<String> = MutableLiveData("")
    fun getNotTxt():LiveData<String> = notTxtLivedata
    var notPage = 0



    fun getFragment():LiveData<String> = fragmentLiveData

    val eventlivedata = MutableLiveData(Constants.EVENT_NONE)
    fun getEvent(): LiveData<Int?> = eventlivedata




    fun loadAllQuestions(context: Context) {
        questionItems.clear()
        val jsonStr = loadJSONFromAsset(context)
        Log.e("json",jsonStr)

        try {

            val jsonObject = JSONObject(jsonStr)
            var fragment = jsonObject.getString("fragmentClassName")
            Log.e("frag",fragment)
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
            } else if(fragment.equals("FragmentTrueOrFalse")){
                val questions = jsonObject.getJSONObject("parameters")
                val questionString = questions.getString("question")
                val answers = questions.getJSONArray("answers")
                val answerlist = ArrayList<Statement>()
                for (i in 0..answers.length()-1){
                    var obj = answers.getJSONObject(i)
                    var statement = obj.getString("statement")
                    var status = obj.getString("status")
                    answerlist.add(Statement(statement,status))
                }
                val leftbutton = questions.getString("button_left")
                val rightbutton = questions.getString("button_right")
                var tofdata = TrueOrFalseModel(questionString,answerlist,leftbutton,rightbutton)
                Log.e("TOF",tofdata.list.size.toString())
                ToFData?.value = tofdata
                ToFlist.value = tofdata.list
            }else if(fragment.equals("FragmentOperation")){
                val parameter = jsonObject.getJSONObject("parameters")
                val text1 = parameter.getString("text1")
                val text2 = parameter.getString("text2")
                val text3 = parameter.getString("text3")
                val text4 = parameter.getString("text4")
                val text5 = parameter.getString("text5")
                val text6 = parameter.getString("text6")
                Collections.addAll(notItems,text1,text2,text3,text4,text5,text6)
                notTxtLivedata.value=notItems[notPage]
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
