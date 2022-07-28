package com.example.simmone.view.activities
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.simmone.ProgressManager
import com.example.simmone.R
import com.example.simmone.dataStore.StorageManager
import com.example.simmone.dataStore.dataStore
import com.example.simmone.databinding.ActivityMainBinding
import com.example.simmone.utils.AppUtil
import com.example.simmone.utils.Plants.plantGrowthIntervals
import com.example.simmone.utils.Plants.plantImages
import com.example.simmone.viewmodel.MainViewModel
import com.google.android.material.math.MathUtils.lerp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  lateinit var storageManager: StorageManager
    private lateinit var mainBinding: ActivityMainBinding
    lateinit var appUtil: AppUtil
    private val mainModel : MainViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        storageManager = StorageManager(dataStore)
        observeSessionNumber()
        appUtil = AppUtil(this)
        appUtil.setDarkMode()
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.button_press)

        mainBinding.cvLaunch.setOnClickListener{
            if(mp.isPlaying) {
                mp.pause() // Pause the current track
            }
            mp.start()  // play sound

            val intent = Intent(this, SplashScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right,0)
        }


        GlobalScope.launch {
            storageManager.storePlantState(0)
            storageManager.storePlantType(1)
        }

        setPlant()

//        val modalBottomSheet = RightBottomSheetDialog()
//        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
    }

    private fun observeSessionNumber() {
        storageManager.sessionCountFlow.asLiveData().observe(this) {
            if (it != null) {
                ProgressManager.instance.sessionNumber = it
                mainBinding.tvGold.text = it.toString()
            }
        }
    }


    private fun setPlant(){
        var sessionsCompleted: Int
        var plantGrowthIndex = 0
        var numberOfPlantsCollected = 0
        var plantType = 0
        var plantState = 0


        GlobalScope.launch {
            plantType = storageManager.getPlantType() ?: 0
            plantState = storageManager.getPlantState() ?: 0
            Log.d("plant_type", plantType.toString())
            Log.d("plant_state", plantState.toString())
        }
        storageManager.sessionCountFlow.asLiveData().observe(this){
            if (it != null) {
                sessionsCompleted = it
                Log.d("plant_sessionsCompletedIt", sessionsCompleted.toString())

                // Converts sessionsCompleted into a plant growth size depending on sessions done
                // Basically a % but with variable divisor
                while (sessionsCompleted > plantGrowthIntervals[plantGrowthIndex]) {
                    sessionsCompleted -= plantGrowthIntervals[plantGrowthIndex]
                    plantGrowthIndex++
                    numberOfPlantsCollected++
                    if (plantGrowthIndex >= plantGrowthIntervals.size) {  // don't go out of index bounds
                        plantGrowthIndex = plantGrowthIntervals.size - 1
                    }
                }

                // normalizes growth progress to maximum growth allowed in this interval
                val plantStageFloat = lerp(
                    0f,
                    5f,
                    sessionsCompleted.toFloat() / plantGrowthIntervals[plantGrowthIndex]
                )
                // turn the number back into int
                sessionsCompleted = plantStageFloat.roundToInt()
                Log.d("plant_growth", sessionsCompleted.toString())


                GlobalScope.launch {
                    storageManager.storePlantGrowth(sessionsCompleted)
                }

                mainBinding.ivPlantMain.setImageResource(plantImages[plantType][plantState][sessionsCompleted])
            }
        }
    }
}
