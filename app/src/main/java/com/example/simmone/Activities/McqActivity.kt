package com.example.simmone.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simmone.Data.QuestionItem
import com.example.simmone.databinding.ActivityMcqBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class McqActivity : AppCompatActivity() {
    private lateinit var mcqBinding: ActivityMcqBinding
    private var currentQuestion = 0
    private var correct = 0
    private var incorrect = 1
    private var questionItems: ArrayList<QuestionItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mcqBinding = ActivityMcqBinding.inflate(layoutInflater)
        setContentView(mcqBinding.root)

        //load all questions
        this.loadAllQuestions()

        //load first question
        setQuestionOnPage(currentQuestion)

        mcqBinding.btChoice1.setOnClickListener { //check if the answer is correct
            if (questionItems[currentQuestion].choice1 == questionItems[currentQuestion].answer
            ) {
                correct++
                Toast.makeText(this@McqActivity, "Well done, right answer", Toast.LENGTH_SHORT)
                    .show()
            } else {
                incorrect++
                Toast.makeText(this@McqActivity, "Oops, incorrect", Toast.LENGTH_SHORT).show()
            }

            // load next question if any
            if (currentQuestion < questionItems.size - 1) {
                currentQuestion++
                setQuestionOnPage(currentQuestion)
            } else {
                Toast.makeText(this@McqActivity, "next", Toast.LENGTH_SHORT).show()

            }
        }
        //click handling
        mcqBinding.btChoice2.setOnClickListener { //check if the answer is correct
            if (questionItems[currentQuestion].choice2 == questionItems[currentQuestion].answer
            ) {
                correct++
                Toast.makeText(this@McqActivity, "Well done, right answer", Toast.LENGTH_SHORT)
                    .show()
            } else {
                incorrect++
                Toast.makeText(this@McqActivity, "Oops, incorrect", Toast.LENGTH_SHORT).show()
            }

            // load next question if any
            if (currentQuestion < questionItems.size - 1) {
                currentQuestion++
                setQuestionOnPage(currentQuestion)
            } else {
                Toast.makeText(this@McqActivity, "Game over", Toast.LENGTH_SHORT).show()
            }
        }
        mcqBinding.btChoice3.setOnClickListener { //check if the answer is correct
            if (questionItems[currentQuestion].choice3 == questionItems[currentQuestion].answer
            ) {
                correct++
                Toast.makeText(this@McqActivity, "Well done, right answer", Toast.LENGTH_SHORT)
                    .show()
            } else {
                incorrect++
                Toast.makeText(this@McqActivity, "Oops, incorrect", Toast.LENGTH_SHORT).show()
            }
            // load next question if any
            if (currentQuestion < questionItems.size - 1) {
                currentQuestion++
                setQuestionOnPage(currentQuestion)
            } else {
                Toast.makeText(this@McqActivity, "Game over", Toast.LENGTH_SHORT).show()
            }
        }
        mcqBinding.btChoice4.setOnClickListener { //check if the answer is correct
            if (questionItems[currentQuestion].choice4 == questionItems[currentQuestion].answer
            ) {
                correct++
                Toast.makeText(this@McqActivity, "Well done, right answer", Toast.LENGTH_SHORT)
                    .show()
            } else {
                incorrect++
                Toast.makeText(this@McqActivity, "Oops, incorrect", Toast.LENGTH_SHORT).show()
            }

            // load next question if any
            if (currentQuestion < questionItems.size - 1) {
                currentQuestion++
                setQuestionOnPage(currentQuestion)
            } else Toast.makeText(this@McqActivity, "Game over", Toast.LENGTH_SHORT).show()
        }


    }
    private fun setQuestionOnPage(number: Int) {
        mcqBinding.tvQuestion.text = questionItems[number].question
        mcqBinding.btChoice1.text = questionItems[number].choice1
        mcqBinding.btChoice2.text = questionItems[number].choice2
        mcqBinding.btChoice3.text = questionItems[number].choice3
        mcqBinding.btChoice4.text = questionItems[number].choice4
    }

    private fun loadAllQuestions(){
        questionItems = ArrayList()
        val jsonStr = "questions.json".loadJSONFromAsset()

        // load all data into list
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
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun String.loadJSONFromAsset(): String {
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            val `is` = assets.open(this)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

}
