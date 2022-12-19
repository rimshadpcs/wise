package com.intractable.simm.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.databinding.ActivityPlantStageBinding
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.PlantStageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlantStageActivity : AppCompatActivity() {
    lateinit var appUtil: AppUtil
    private lateinit var plantStageBinding: ActivityPlantStageBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantStageBinding = ActivityPlantStageBinding.inflate(layoutInflater)
        setContentView(plantStageBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        val plantStageViewModel = ViewModelProvider(this)[PlantStageViewModel::class.java]


        plantStageViewModel.plantStageFlow.observe(this@PlantStageActivity){
            val stage = "Stage "
            val plantsIhave = it.first.toString()
            val bar = "/"
            val totalPlants = it.second.toString()

            plantStageBinding.tvStage.text = stage + plantsIhave + bar + totalPlants
        }


        plantStageViewModel.plantNameFlow.observe(this){
            plantStageBinding.tvPlantName.text = it
        }
        plantStageViewModel.plantImageFlow.observe(this@PlantStageActivity){
            plantStageBinding.myPlant.setImageResource(it)
        }
        plantStageViewModel.plantStageComment.observe(this){
            plantStageBinding.tvPlantStageDescription.text =it

            lifecycleScope.launch (Dispatchers.Main){
                delay(1000)

                plantStageBinding.tvPlantStageDescription.fadeVisibility(View.VISIBLE)

            }

        }

        plantStageBinding.ivBackButton.setOnClickListener {
            finish()
        }

    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 1000) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

}