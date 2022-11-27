package com.intractable.simm.view.activities
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityOnboardingBinding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OnBoardingActivity : AppCompatActivity(){
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var onBoardBinding: ActivityOnboardingBinding
    lateinit var appUtil: AppUtil

    private var sharedPreferences: SharedPreferences?=null
    private lateinit var viewBinding: ActivityOnboardingBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.soft_notification)
        val viewModel = ViewModelProvider(this)[OnBoardingViewModel::class.java]
        firebaseAnalytics.logEvent("simm_onboarding_started", null)
        viewBinding = ActivityOnboardingBinding.inflate(layoutInflater)


        Config.instance.progressManager.checkAndUpdateAppIfNeeded()


        if(restorePrefData()){
            val i = Intent(applicationContext,HomeActivity::class.java )
            startActivity(i)
            finish()
        }

        onBoardBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(onBoardBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()


        onBoardBinding.btContinueOnBoarding.setOnClickListener {
            firebaseAnalytics.logEvent("simm_onboarding_completed", null)


            savePrefData()
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
            finish()
        }
        onBoardBinding.btContinueOnBoarding.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    onBoardBinding.btContinueOnBoarding.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        onBoardBinding.btContinueOnBoarding.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    onBoardBinding.btContinueOnBoarding.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    onBoardBinding.btContinueOnBoarding.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        onBoardBinding.btContinueOnBoarding.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    onBoardBinding.btContinueOnBoarding.layoutParams = params
                }
            }
            false
        }


        val onboardingActivityOwner = this

        onBoardBinding.etUserName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    val userName = onBoardBinding.etUserName.text.toString()
                    val message1 = "Hey $userName!"

                    Config.instance.progressManager.storeUserName(userName)

                    onBoardBinding.tvWhatToCall.text = message1
                    if(mp.isPlaying) {
                        mp.pause() // Pause the current track
                    }
                    mp.start()  // play sound

                    val ims = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    ims.hideSoftInputFromWindow(onBoardBinding.etUserName.windowToken, 0)

                    onBoardBinding.etUserName.visibility = View.INVISIBLE

                    lifecycleScope.launch (Dispatchers.Main){
                        delay(2000)

                        val i = Intent(applicationContext, OnBoardingComicActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                    return true
                }
                return false

            }
        })

    }

    private fun savePrefData(){

        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor!!.putBoolean("isFirstTime", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTime",false)
    }

    suspend fun storeUserName(input: String){
        Config.instance.storageManager?.storeUserInputName(input)

    }
}