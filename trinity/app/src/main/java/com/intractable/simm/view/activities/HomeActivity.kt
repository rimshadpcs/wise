package com.intractable.simm.view.activities

//import com.facebook.FacebookSdk

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.adapters.HomeAdapter
import com.intractable.simm.databinding.ActivityHomeBinding
import com.intractable.simm.databinding.HomeBinding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.SessionModel
import com.intractable.simm.utils.AnimationControllerImpl
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.TextUtil
import com.intractable.simm.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class HomeActivity : AppCompatActivity(),
    HomeAdapter.DataPassInterface {
    private val TAG = "HomeActivity"



//    val mpC : MediaPlayer = MediaPlayer.create(this, R.raw.sparkle_plant_growth_small)
//    val mpEG : MediaPlayer = MediaPlayer.create(this, R.raw.sparkle_plant_count)

    companion object{
        const val DURATION = 500L
    }


    private lateinit var homeActivityBinding: ActivityHomeBinding
    private var homeAdapter: HomeAdapter? = null
    lateinit var appUtil: AppUtil
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var homeViewModel: HomeViewModel
    private val milliSecondsPerInch = 200f


    private var hasPlantStageSparkle = false
    private var simmComment = ""

    private lateinit var updateAnimationsPosition: ViewTreeObserver.OnGlobalLayoutListener
    private lateinit var mpMessage: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDynamicLink()
        homeActivityBinding = com.intractable.simm.databinding.ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)
        firebaseAnalytics = Firebase.analytics
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val mpC : MediaPlayer = MediaPlayer.create(this, R.raw.sparkle_plant_growth_small)
        val mpEG : MediaPlayer = MediaPlayer.create(this, R.raw.sparkle_plant_count)
        mpMessage = MediaPlayer.create(this, R.raw.simm_message)

        var notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var channel = notificationManager.getNotificationChannel("channel_ID_1")

        updateAnimationsPosition = ViewTreeObserver.OnGlobalLayoutListener {
            val sparkleTranslation = homeActivityBinding.plantStageView.x + homeActivityBinding.tvBuddyCount.x + homeActivityBinding.tvBuddyCount.width/2 - homeActivityBinding.lottieBuddyCount.width/2
            Log.i(TAG, "onGlobalLayout() sparkleTranslation $sparkleTranslation homeActivityBinding.tvBuddyCount.width ${homeActivityBinding.tvBuddyCount.width} homeActivityBinding.lottieBuddyCount.width ${homeActivityBinding.lottieBuddyCount.width}")
            homeActivityBinding.lottieBuddyCount.translationX = sparkleTranslation

            clearCallBacks()
        }
        homeActivityBinding.clDashbaoard.viewTreeObserver.addOnGlobalLayoutListener(updateAnimationsPosition)

        //Log.i(TAG, "onCreate() FB SDK initialized " + FacebookSdk.isInitialized())


//        homeViewModel.plantCountFlow.observe(this) {
//            if (it != null) homeActivityBinding.tvSocial.text = it.toString()
//        }

        homeViewModel.heartCountFlow.observe(this) {

            homeActivityBinding.tvHeartCount.text = it.toString()

            if (it == 0) {
                showBadHeartBottomSheet()
            }
        }


        homeActivityBinding.tvHeartCount.setOnClickListener {
            showGoodHeartBottomSheet()
        }

        homeActivityBinding.ivHeartIcon.setOnClickListener {
            showGoodHeartBottomSheet()
        }


        homeViewModel.plantStageFlow.observe(this) {
            val plantsIhave = it.first.toString()
            val bar = "/"
            val totalPlants = it.second.toString()
            homeActivityBinding.tvBuddyCount.text = plantsIhave + bar + totalPlants
        }

        homeViewModel.sparkleFlow.observe(this) {


            val animationController = AnimationControllerImpl()
            if (it != null) {

                if(it.first!=null){
                    animationController.addAnimation(homeActivityBinding.lottieBuddyCount, mpC)
                }

                }

                animationController.setListener {
                    //play()
                    finishSparkle()
                    playSimmCommentAnimation()
                }

                if (it?.second != null) {
                    simmComment = it.second!!
                }

            animationController.play()
            }



        val appContext = Config.instance.applicationContext!!


        val glanceId = appContext.resources.getIdentifier("simm_blink_glance", "raw", appContext.packageName)
        val blinkId = appContext.resources.getIdentifier("simm_idleblink", "raw", appContext.packageName)

        homeActivityBinding.ivSimmMain.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                handleSimmAnimation(glanceId, blinkId)
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
        homeActivityBinding.ivSimmMain.playAnimation()


        homeViewModel.sessionListFlow.observe(this) {
            // val homeItems = homeViewModel.generateLessons(it)
            if (it != null) {
                for (lesson in it) {

                    homeAdapter = HomeAdapter(this,it, layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false), recyclerView = homeActivityBinding.rvHome)

                    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                    homeActivityBinding.rvHome.layoutManager =
                        LinearLayoutManager(this, layoutManager.orientation,false)
                    homeAdapter!!.setDataToPass(this)
                    homeActivityBinding.rvHome.adapter = homeAdapter
                    homeActivityBinding.rvHome.hasFixedSize()

                    if (lesson.hasPlayIcon) {
                        homeActivityBinding.tvDescription.text = TextUtil.updateTextWithIcons(lesson.comment, this, homeActivityBinding.tvDescription.paint.fontMetrics)
                    }
                }
            }
        }

        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        homeActivityBinding.ivSocialIcon.setOnClickListener {
            val intent = Intent(this@HomeActivity, SocialActivity::class.java)
            startActivity(intent)
        }

        homeActivityBinding.ivSettings.setOnClickListener {
            val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        homeActivityBinding.tvBuddyCount.setOnClickListener{
            val intent = Intent(this@HomeActivity, PlantedexActivity::class.java)
            startActivity(intent)
        }
        homeActivityBinding.ivPlantidexIcon.setOnClickListener {
            val intent = Intent(this@HomeActivity, PlantedexActivity::class.java)
            startActivity(intent)
        }

        if (intent.extras != null) {
            val b = intent.extras
            val launchStation = b!!.getBoolean("cosmodrome")

            if (launchStation){
                firebaseAnalytics = Firebase.analytics
                firebaseAnalytics.logEvent("simm_daily_notification_opened", null)
            }

        }


    }

    private fun getDynamicLink(){
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.i("Deep link",deepLink.toString())
                    var intent = Intent(this,SocialActivity::class.java)
                    intent.putExtra("data","dynamic_link")
                    startActivity(intent)

                }

            }
            .addOnFailureListener(
                this
            ) { e -> Log.w("Dynamic Link", "getDynamicLink:onFailure", e) }

    }


    private fun clearCallBacks() {
        homeActivityBinding.clDashbaoard.viewTreeObserver.removeOnGlobalLayoutListener(updateAnimationsPosition)
    }

    private fun finishSparkle() {
        hasPlantStageSparkle = false
        //hasPlantCountSparkle = false
        homeViewModel.finishSparkle()
    }

    @SuppressLint("DiscouragedApi")
    private fun playSimmCommentAnimation() {
        Log.i(TAG, "playSimmCommentAnimation() starting animation")
        val appContext = Config.instance.applicationContext!!

        val simmSpeakingWithExclamation = appContext.resources.getIdentifier("simmtalking_exclamationmark", "raw", appContext.packageName)
        val simmSpeakingWithLines = appContext.resources.getIdentifier("simmtalking_lines", "raw", appContext.packageName)

        val writer = homeActivityBinding.tvDescription
        writer.setCharacterDelay(100)
        writer.animateText(simmComment)
        homeActivityBinding.ivSimmMain.cancelAnimation()
        val random = Random.Default.nextInt(2)
        if (random == 0) {
            homeActivityBinding.ivSimmMain.setAnimation(simmSpeakingWithExclamation)
        } else {
            homeActivityBinding.ivSimmMain.setAnimation(simmSpeakingWithLines)
        }


        homeActivityBinding.ivSimmMain.playAnimation()



        // play sound effect
        if (mpMessage.isPlaying) {
            mpMessage.pause() // Pause the current track
        }
        mpMessage.start()  // play sound
    }

    var simmAnimationState = SimmAnimationState.Blink
    enum class SimmAnimationState {
        Blink, Glance, SpeakingWithExclamation, SpeakingWithLines
    }

    private fun handleSimmAnimation(glanceId: Int, blinkId: Int) {
        val random = Random.Default.nextInt(10)
        if (random < 4) {
            // Glance
            homeActivityBinding.ivSimmMain.setAnimation(glanceId)
            Log.i(TAG, "playing Simm glance animation [$random]")
        }
        else {
            // Blink
            homeActivityBinding.ivSimmMain.setAnimation(blinkId)
            Log.i(TAG, "playing Simm idle blink animation [$random]")
        }
        homeActivityBinding.ivSimmMain.playAnimation()
    }


    @SuppressLint("SetTextI18n", "InflateParams", "CutPasteId", "ClickableViewAccessibility")
    private fun showGoodHeartBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.hearts_bottom_sheet, null)
        dialog.setContentView(view)
        dialog.show()
        val tvHeartMessage = view.findViewById<TextView>(R.id.tv_heart_message)
        val heartParentLayout = view.findViewById<ConstraintLayout>(R.id.heart_parent_layout)
        val btOkk= view.findViewById<Button>(R.id.btOk)

        var mpHearts : MediaPlayer = MediaPlayer.create(this, R.raw.health_regen)

        val observer: Observer<Int?> = Observer<Int?> {
            if (it != null) {
                if (it > 0) {
                    tvHeartMessage.text = "You have $it hearts"
                    btOkk.setTextColor(ContextCompat.getColor(this,R.color.candy_green))

                    heartParentLayout.setBackgroundResource(R.drawable.round_corners_green)

                    val bottomSheet = view.parent as View
                    bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
                    bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                    bottomSheet.setBackgroundColor(Color.TRANSPARENT)

                    // play sound effect
                    if(mpHearts.isPlaying) {
                        mpHearts.pause() // Pause the current track
                    }
                    mpHearts.release()
                    mpHearts = MediaPlayer.create(this, R.raw.health_regen)
                    mpHearts.start()  // play sound

                } else if(it.equals(0)){
                    tvHeartMessage.text = "You don't have any hearts left to learn"
                    val bottomSheet = view.parent as View
                    bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
                    bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                    bottomSheet.setBackgroundColor(Color.TRANSPARENT)
                    heartParentLayout.setBackgroundResource(R.drawable.round_corners_green)
                    btOkk.setTextColor(ContextCompat.getColor(this,R.color.candy_green))

                    // play sound effect
                    if(mpHearts.isPlaying) {
                        mpHearts.pause() // Pause the current track
                    }
                    mpHearts.release()
                    mpHearts = MediaPlayer.create(this, R.raw.minigame_fail)
                    mpHearts.start()  // play sound
                }
            }
        }
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.heartCountFlow.observe(this, observer)
        dialog.setOnDismissListener { homeViewModel.heartCountFlow.removeObserver(observer) }

        val btOk = view.findViewById<Button>(R.id.btOk)

        btOk.setOnClickListener {

            lifecycleScope.launch (Dispatchers.Main){
                delay(100)
                dialog.dismiss()

            }

        }

        btOk.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btOk.setBackgroundResource(R.drawable.no_shadow_white)
                    val params =
                        btOk.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    btOk.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    btOk.setBackgroundResource(R.drawable.white_shadow_button_bg)
                    val params =
                        btOk.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    btOk.layoutParams = params
                }
            }
            false
        }
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun showBadHeartBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.hearts_bottom_sheet, null)
        dialog.setContentView(view)
        dialog.show()
        val heartParentLayout = view.findViewById<ConstraintLayout>(R.id.heart_parent_layout)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        heartParentLayout.setBackgroundResource(R.drawable.round_corners_red)

        val btOk = view.findViewById<Button>(R.id.btOk)
        btOk.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main){
                delay(100)
                dialog.dismiss()

            }

        }
        btOk.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btOk.setBackgroundResource(R.drawable.no_shadow_white)
                    val params =
                        btOk.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    btOk.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    btOk.setBackgroundResource(R.drawable.white_shadow_button_bg)
                    val params =
                        btOk.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    btOk.layoutParams = params
                }
            }
            false
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    override fun passData(
        homeItems: SessionModel,
        homeBinding: HomeBinding,
        layoutPosition: Int,
        list: ArrayList<SessionModel>
    ) {

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeBinding.tvLessonDescription.setOnClickListener {

            homeViewModel.setSession(index = layoutPosition)
            intent.putExtra("lessonName", layoutPosition)
            startActivity(Intent(this, LockOrUnlockActivity::class.java))
        }


    }
//    var smoothScroller: SmoothScroller = object : LinearSmoothScroller(this) {
//        override fun getVerticalSnapPreference(): Int {
//            return SNAP_TO_START
//        }
//    }

    override fun scrollToPosition(position: Int) {
        homeActivityBinding.rvHome.scrollToPosition(position)
    }


    private fun vibrationTest(){
        val loadTime: Long = 1000000
        val timePerFrame: Long = 2000
        var i = -1
        object : CountDownTimer(loadTime, timePerFrame) {

            override fun onTick(millisUntilFinished: Long) {
                vibratePredefined(i)
                i++
                if(i > 4) i = 1
            }

            override fun onFinish() {

            }
        }.start()
    }
    private fun vibratePredefined(type: Int){
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        // this type of vibration requires API 29
        val vibrationEffect: VibrationEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val singleClickPattern = longArrayOf(0, 10)
            val singleClickAmplitude = intArrayOf(0, 180)

            val doubleClickPattern = longArrayOf(0, 10, 160, 10)
            val doubleClickAmplitude = intArrayOf(0, 255, 0, 255)

            val heavyClickPattern = longArrayOf(0, 13)
            val heavyClickAmplitude = intArrayOf(0, 255)

            val tickPattern = longArrayOf(0, 5)
            val tickAmplitude = intArrayOf(0, 50)

            when (type) {
                1 -> vibrationEffect =
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                2 -> vibrationEffect = VibrationEffect.createWaveform(  // default vibrate length is 1000
                    doubleClickPattern, doubleClickAmplitude, -1
                )
                3 -> vibrationEffect =
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                4 -> vibrationEffect = VibrationEffect.createWaveform(  // default vibrate length is 1000
                    doubleClickPattern, doubleClickAmplitude, -1
                )
                else -> vibrationEffect = VibrationEffect.createOneShot(  // default vibrate length is 1000
                    1,
                    200
                )
            }

            // it is safe to cancel other vibrations currently taking place
            vib.cancel();
            vib.vibrate(vibrationEffect);
        }
    }
}