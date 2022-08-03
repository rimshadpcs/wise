package com.intractable.simm.view.activities
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityOnboardingBinding
import com.intractable.simm.model.OnBoarding
import com.intractable.simm.adapters.OnBoardingAdapter
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var onBoardBinding: ActivityOnboardingBinding
    private var onBoardingAdapter:OnBoardingAdapter? = null
    private val milliSecondsPerInch = 200f
    private var rvOnBoarding: RecyclerView? = null
    lateinit var appUtil: AppUtil

    private var sharedPreferences: SharedPreferences?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(restorePrefData()){
            val i = Intent(applicationContext,MainActivity::class.java )
            startActivity(i)
            finish()
        }

        onBoardBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(onBoardBinding.root)

        appUtil = AppUtil(this)
        appUtil.setDarkMode()
        rvOnBoarding = findViewById(R.id.rvOnBoarding)

        onBoardBinding.btContinueOnBoarding.visibility = View.INVISIBLE
        onBoardBinding.rvOnBoarding.visibility = View.INVISIBLE
        onBoardBinding.btContinueOnBoarding.setOnClickListener {
            savePrefData()
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
        }
        onBoardBinding.etUserName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    onBoardBinding.tvWhatToCall.visibility = View.INVISIBLE
                    executeRecyclerView()
                    val userName = onBoardBinding.etUserName.text.toString()

                        val onBoarding = OnBoarding("Hey $userName!")
                        onBoardingAdapter!!.getItemList().add(0, onBoarding)

                    val ims = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    ims.hideSoftInputFromWindow(onBoardBinding.etUserName.windowToken, 0)

                    onBoardBinding.etUserName.visibility = View.INVISIBLE

                    return true
                }
                return false

            }


        })
    }

    fun executeRecyclerView() {
        val onBoardingViewModel = ViewModelProvider(this)[OnBoardingViewModel::class.java]

        onBoardingViewModel.generateOnBoardingMessage()
        onBoardBinding.rvOnBoarding.visibility = View.VISIBLE
        onBoardingViewModel.sizeOfMessageList
        onBoardingViewModel.newMOnboardingList.observe(this@OnBoardingActivity) {
            val animId = R.anim.animation_from_bottom_layout
            val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this@OnBoardingActivity, animId)
            onBoardBinding.rvOnBoarding.layoutAnimation = animation

            onBoardingAdapter = OnBoardingAdapter(it)
            val linearLayoutManager = LinearLayoutManager(this@OnBoardingActivity)
            linearLayoutManager.stackFromEnd = true
            linearLayoutManager.reverseLayout = false
            onBoardBinding.rvOnBoarding.layoutManager = linearLayoutManager
            onBoardBinding.rvOnBoarding.adapter = onBoardingAdapter
            onBoardingAdapter!!.getItemList().size
            val sizeOfTheList = onBoardingViewModel.sizeOfMessageList.value

            val delayTimes = longArrayOf(2000, 4000, 6000)

            lifecycleScope.launch(Dispatchers.Main) {
                val smoothScroller: SmoothScroller =
                    object : LinearSmoothScroller(this@OnBoardingActivity) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return milliSecondsPerInch / displayMetrics.densityDpi
                        }
                    }
                for (i in 0 until sizeOfTheList!!) {
                    val delayTime = delayTimes[min(i, delayTimes.size - 1)]
                    delay(delayTime)
                    onBoardingAdapter!!.notifyDataSetChanged()
                    onBoardBinding.rvOnBoarding.run { smoothScroller.targetPosition = min(i+1, sizeOfTheList)

                        (onBoardBinding.rvOnBoarding.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)
                    }
                        if (i == sizeOfTheList - 2) {
                        onBoardBinding.btContinueOnBoarding.visibility = View.VISIBLE

                    }
                }
            }
        }
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
}