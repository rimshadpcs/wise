package com.example.simmone.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.simmone.model.QuestionItem
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
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
/*
    class MoshiJsonParseTester{
        fun parse(reader: JsonReader): List<QuestionItem>{
            val questions = mutableListOf<QuestionItem>()

            reader.beginArray()
            while (reader.hasNext()) {
                var question: String  = ""
                var choice1: String = ""
                var choice2: String = ""
                var choice3: String = ""
                var choice4: String = ""
                var answer: String = ""

                reader.beginObject()
                while (reader.hasNext()){
                    when (reader.nextName()) {
                        question -> question = reader.nextName()
                        choice1 -> choice1 = reader.nextName()
                        choice2 -> choice2 = reader.nextName()
                        choice3 -> choice3 = reader.nextName()
                        choice4 -> choice4 = reader.nextName()
                    }
                }
                reader.endObject()

                if (question == ""|| choice1 == ""){
                    throw JsonDataException("Missing required field")
                }
                val questionItem = QuestionItem(question, choice1,choice2,choice3,choice4,answer)
                questions.add(questionItem)
            }
            reader.endArray()
            return questions
        }
    }*/
}