package com.example.simmone.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.simmone.model.QuestionItem
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
class McqViewModel: ViewModel() {

    private var questionItems: ArrayList<QuestionItem> = ArrayList()

    val mcqData = MutableLiveData<List<QuestionItem>>()
    fun getMcQData(): ArrayList<QuestionItem> = questionItems

    var quizFile = MutableLiveData<String>()


    fun loadAllQuestions(context: Context) {
        val jsonStr = loadJSONFromAsset(context)

        questionItems = ArrayList()

        try {
            val jsonObject = JSONObject(jsonStr)
            val questions = jsonObject.getJSONArray("questions")
            for (i in 0 until questions.length()) {
                val question = questions.getJSONObject(i)
                val questionString = question.getString("question")
                val choice1String = question.getString("choice1")
                val choice2String = question.getString("choice2")
                val choice3String = question.getString("choice3")
                val choice4String = question.getString("choice4")
                val answer = question.getString("answer")
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
            }

            mcqData.value = questionItems
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun loadJSONFromAsset(context: Context): String {
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            if(quizFile.value != null){
                val `is` = context.assets.open(quizFile.value!!)
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
}