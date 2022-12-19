package com.example.testpresso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testpresso.databinding.ActivityBravoBinding
import com.example.testpresso.databinding.ActivityMainBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.btHappy.setOnClickListener {

            activityMainBinding.tvHappy.text = activityMainBinding.btHappy.text
//            val intent = Intent(this,BravoActivity::class.java )
//            startActivity(intent)

        }




    }
}