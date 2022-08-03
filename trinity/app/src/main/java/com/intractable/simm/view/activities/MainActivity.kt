package com.intractable.simm.view.activities
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.intractable.simm.view.widgets.PlantWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private lateinit var storageManager: StorageManager
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

        mainBinding.ivSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right,0)
        }

        mainBinding.tvSimmComment.visibility = View.INVISIBLE
        mainBinding.ivSimmMain.setOnClickListener {
            // Display text bubble with Simm comment
            mainBinding.tvSimmComment.visibility = View.VISIBLE
            mainBinding.tvSimmComment.text = "Hi I am Simm!"

            val loadTime: Long = 3000
            val timePerFrame: Long = 16
            object : CountDownTimer(loadTime, timePerFrame) {

                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    mainBinding.tvSimmComment.visibility = View.INVISIBLE
                }
            }.start()
        }

        storageManager.plantImageFlow.asLiveData().observe(this) {
            Log.i("MainActivity", "plantImage = $it")
            if (it != null) {
                mainBinding.ivPlantMain.setImageResource(it)
            }
        }

        storageManager.plantCountFlow.asLiveData().observe(this) {
            Log.i("MainActivity", "plantCount = $it")
            if (it != null) {
                mainBinding.tvPlantCount.text = it.toString()
            }
        }

//        val modalBottomSheet = RightBottomSheetDialog()
//        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
    }


    private fun observeSessionNumber() {
        storageManager.sessionCountFlow.asLiveData().observe(this) {
            if (it != null) {
                com.intractable.simm.ProgressManager.instance.sessionNumber = it
                if (mainModel.activityList.isNotEmpty() && it<mainModel.activityList.size) {
                    mainBinding.tvSessionName.text = mainModel.activityList[it]
                }else{
                    mainBinding.tvSessionName.text = "Coming Soon"
                    mainBinding.cvLaunch.isClickable = false
                }
                Log.e("Session","fd")
            }
            else
                mainBinding.tvSessionName.text = mainModel.activityList[0]
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun setPlant(){
        var sessionsCompleted: Int
        var plantGrowthIndex = 0
        var numberOfPlantsCollected = 0
        var plantType: Int
        var plantState: Int

        GlobalScope.launch {
            plantType = storageManager.getPlantType() ?: 0
            plantState = storageManager.getPlantState() ?: 0
            sessionsCompleted = storageManager.getSessionNumber() ?: 0
            Log.d("plant_type", plantType.toString())
            Log.d("plant_state", plantState.toString())

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


            storageManager.storePlantGrowth(sessionsCompleted)
            storageManager.storePlantCount(numberOfPlantsCollected)

            mainBinding.ivPlantMain.setImageResource(plantImages[plantType][plantState][sessionsCompleted])
            mainBinding.tvPlantCount.text = numberOfPlantsCollected.toString()


            setWidget(plantType, plantState, sessionsCompleted)
        }
    }

    private fun setWidget(plantType : Int, plantState : Int, plantGrowth : Int) {

        // PlantWidget
        val context: Context = applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.plant_widget)
        val thisWidget = ComponentName(context, PlantWidget::class.java)
        remoteViews.setImageViewResource(R.id.iv_plant, plantImages[plantType][plantState][plantGrowth])
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    private fun setButtonTexts(){


    }
}