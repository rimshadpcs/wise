package com.intractable.simm.view.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.intractable.simm.R
import com.intractable.simm.adapters.PlantdexAdapter
import com.intractable.simm.databinding.ActivityPlantedexBinding
import com.intractable.simm.model.PlantItem
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.MiddleDividerItemDecoration
import com.intractable.simm.utils.Plants
import com.intractable.simm.viewmodel.PlantdexViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlantedexActivity : AppCompatActivity() {
    private lateinit var plantdexActivityBinding: ActivityPlantedexBinding
    private var plantdexAdapter: PlantdexAdapter?=null
    lateinit var appUtil: AppUtil
    //@Inject lateinit var plantdexViewModel: PlantdexViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantdexActivityBinding = ActivityPlantedexBinding.inflate(layoutInflater)
        setContentView(plantdexActivityBinding.root)

        appUtil = AppUtil(this)
        appUtil.setDarkMode()



        val plantdexViewModel = ViewModelProvider(this)[PlantdexViewModel::class.java]

        plantdexViewModel.plantStageFlow.observe(this@PlantedexActivity){
            val stage = "Stage "
            val plantsIhave = it.first.toString()
            val bar = "/"
            val totalPlants = it.second.toString()

            plantdexActivityBinding.tvPlantStageCount.text = stage + plantsIhave + bar + totalPlants
        }

        plantdexViewModel.plantNameFlow.observe(this){
            plantdexActivityBinding.tvPlantName.text = it
        }
        plantdexViewModel.plantImageFlow.observe(this@PlantedexActivity){
            plantdexActivityBinding.myPlant.setImageResource(it)
        }
        
        plantdexViewModel.plantCountFlow.observe(this@PlantedexActivity){

            val dummyPlant = PlantItem(R.drawable.dex_plant_silhouette)
            val collected = it.size

            for(i in collected until Plants.dexSilhouettes.size){  // adding made plants
                it.add(PlantItem(Plants.dexSilhouettes[i]))

            }

            for (i in Plants.dexSilhouettes.size until 30){  // adding spaces for plants to be made
                if(i % 7 == 0){
                    it.add(dummyPlant)
                }
                else{
                    it.add(PlantItem(Plants.dexSilhouettes[i % Plants.dexSilhouettes.size]))
                }
            }

            plantdexAdapter = PlantdexAdapter(this,it)
            val itemDecoration = MiddleDividerItemDecoration(this, MiddleDividerItemDecoration.ALL)
            itemDecoration.setDividerColor(ContextCompat.getColor(this, android.R.color.black))
            plantdexActivityBinding.rvPlantdex.addItemDecoration(itemDecoration)
            plantdexActivityBinding.rvPlantdex.layoutManager = GridLayoutManager(this@PlantedexActivity,3)
            plantdexActivityBinding.rvPlantdex.adapter = plantdexAdapter

        }

        plantdexActivityBinding.ivBackButton.setOnClickListener {
            finish()
        }

        plantdexViewModel.plantCollectionCommentFlow.observe(this){
            plantdexActivityBinding.tvMessage.text = it
            lifecycleScope.launch (Dispatchers.Main){
                delay(1000)

                plantdexActivityBinding.tvMessage.fadeVisibility(View.VISIBLE)

            }
        }
        plantdexViewModel.plantTotalCount.observe(this){
            val plantsIHave = it.first
            val totalPlants = it.second
            plantdexActivityBinding.tvPlantCount.text = "$plantsIHave/$totalPlants"

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