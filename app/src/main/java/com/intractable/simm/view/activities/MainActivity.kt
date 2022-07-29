package com.intractable.simm.view.activities
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.dataStore.dataStore
import com.intractable.simm.databinding.ActivityMainBinding
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.Plants.plantGrowthIntervals
import com.intractable.simm.utils.Plants.plantImages
import com.intractable.simm.viewmodel.MainViewModel
import com.google.android.material.math.MathUtils.lerp
import com.intractable.simm.utils.Constants
import com.intractable.simm.utils.Plants
import com.intractable.simm.view.widgets.PlantWidget
import com.intractable.simm.viewmodel.SessionViewModel
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
        mainModel.loadSession(this)
        observeSessionNumber()
        appUtil = AppUtil(this)
        appUtil.setDarkMode()
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.button_press)

        setButtonTexts()

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
                com.intractable.simm.ProgressManager.instance.sessionNumber = it
                if (!mainModel.activityList.isEmpty())
                mainBinding.tvSessionName.text = mainModel.activityList[it]
                Log.e("Session","fd")
            }
            else
                mainBinding.tvSessionName.text = mainModel.activityList[0]
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
                    storageManager.storePlantCount(numberOfPlantsCollected)
                }

                mainBinding.ivPlantMain.setImageResource(plantImages[plantType][plantState][sessionsCompleted])
                mainBinding.tvPlantCount.text = numberOfPlantsCollected.toString()


                setWidget(plantType, plantState, sessionsCompleted)
            }
        }
    }

    private fun setWidget(plantType : Int, plantState : Int, plantGrowth : Int) {

        // PlantWidget
        val context: Context = applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.plant_widget)
        val thisWidget = ComponentName(context, PlantWidget::class.java)
        remoteViews.setImageViewResource(R.id.iv_plant, Plants.plantImages[plantType][plantState][plantGrowth])
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    private fun setButtonTexts(){


    }
}
