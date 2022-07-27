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
import com.example.simmone.viewmodel.MainViewModel
import com.google.android.material.math.MathUtils.lerp
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  lateinit var storageManager: StorageManager
    private lateinit var mainBinding: ActivityMainBinding
    lateinit var appUtil: AppUtil
    private val mainModel : MainViewModel by viewModels()

    // store drawable image references here
    // will probably change into 2D array when more plants are made
    // 0 == purple, 1 == red, 2 == yellow
    // 0 == awake, 1 == asleep, 2 == surprised
    // tulip_red_**_stage0-2 can be used for tulip_**_**_stage 0-2 to reduce app size
    private val plantImages = arrayOf(
        arrayOf(  // purple
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_purple_awake_stage3,
                R.drawable.tulip_purple_awake_stage4,
                R.drawable.tulip_purple_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_purple_asleep_stage3,
                R.drawable.tulip_purple_asleep_stage4,
                R.drawable.tulip_purple_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_purple_surprised_stage3,
                R.drawable.tulip_purple_surprised_stage4,
                R.drawable.tulip_purple_surprised_stage5
            )
        ),
        arrayOf(  //red
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_red_awake_stage3,
                R.drawable.tulip_red_awake_stage4,
                R.drawable.tulip_red_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_red_asleep_stage3,
                R.drawable.tulip_red_asleep_stage4,
                R.drawable.tulip_red_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_red_surprised_stage3,
                R.drawable.tulip_red_surprised_stage4,
                R.drawable.tulip_red_surprised_stage5
            )
        ),
        arrayOf(  // yellow
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_yellow_awake_stage3,
                R.drawable.tulip_yellow_awake_stage4,
                R.drawable.tulip_yellow_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_yellow_asleep_stage3,
                R.drawable.tulip_yellow_asleep_stage4,
                R.drawable.tulip_yellow_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_yellow_surprised_stage3,
                R.drawable.tulip_yellow_surprised_stage4,
                R.drawable.tulip_yellow_surprised_stage5
            )
        )
    )
    // The number of sessions to complete for a fully grown plant
    private val plantGrowthIntervals: MutableList<Int> = mutableListOf(
        plantImages.size - 1
    )

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
        var sessionsCompleted = 0
        var plantGrowthIndex = 0
        var numberOfPlantsCollected = 0
        storageManager.sessionCountFlow.asLiveData().observe(this){
            if (it != null) {
                sessionsCompleted = it
                Log.d("sessionsCompletedIt", sessionsCompleted.toString())

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
                    plantImages.size.toFloat()-1,
                    sessionsCompleted.toFloat() / plantGrowthIntervals[plantGrowthIndex]
                )
                // turn the number back into int
                sessionsCompleted = plantStageFloat.roundToInt()
                Log.d("sessionsCompletedFinal", sessionsCompleted.toString())

                // set the images
                mainBinding.ivPlantMain.setImageResource(plantImages[1][1][sessionsCompleted])
//                mainBinding.ivPlantCounter.text = numberOfPlantsCollected.toString()  // ivPlantCounter is a placeholder, doesn't exist
            }
        }
    }
}
