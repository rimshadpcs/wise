package com.example.simmone.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simmone.R
import com.example.simmone.adapters.OnBoardingAdapter
import com.example.simmone.databinding.ActivityOnboardingBinding
import com.example.simmone.databinding.ActivityOperationBinding
import com.example.simmone.viewmodel.OnBoardingViewModel

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var onBoardBinding: ActivityOnboardingBinding
    private var onBoardingAdapter: OnBoardingAdapter?=null
    private var rvOnBoarding: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBoardBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(onBoardBinding.root)
        rvOnBoarding = findViewById(R.id.rvOnBoarding)

        val onBoardingViewModel = ViewModelProvider(this).get(OnBoardingViewModel::class.java)
        onBoardingViewModel.generateOnBoardingMessage()

        onBoardingViewModel.newMOnboardingList.observe(this){
            onBoardingAdapter = OnBoardingAdapter(this@OnBoardingActivity,it)
            onBoardBinding.rvOnBoarding.layoutManager = LinearLayoutManager(this@OnBoardingActivity)
            onBoardBinding.rvOnBoarding.adapter = onBoardingAdapter

        }
    }
}