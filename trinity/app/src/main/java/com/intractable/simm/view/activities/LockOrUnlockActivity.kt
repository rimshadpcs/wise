package com.intractable.simm.view.activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityLockOrUnlockBinding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.LockOrUnlockViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class LockOrUnlockActivity : AppCompatActivity() {
    lateinit var appUtil: AppUtil
    private lateinit var lockOrUnlockBinding: ActivityLockOrUnlockBinding
    private val TAG = "LockOrUnlockActivity"

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor",
        "ClickableViewAccessibility"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUtil  = AppUtil(this)
        appUtil.setDarkMode()
        lockOrUnlockBinding = ActivityLockOrUnlockBinding.inflate(layoutInflater)
        setContentView(lockOrUnlockBinding.root)

        lockOrUnlockBinding.ivBackButton.setOnClickListener { finish() }
        val lockOrUnlockViewModel = ViewModelProvider(this)[LockOrUnlockViewModel::class.java]
        var displayCharacter = "Simm"

        if (intent.hasExtra("shortcut")){
            lockOrUnlockViewModel.setHighestSession()
        }

        lockOrUnlockViewModel.sessionModelFlow.observe(this){ it ->
            if (it != null) {
                if(it.hasLargeBackgroundImg){
                    lockOrUnlockBinding.ivLargeBackground.setImageResource(it.largeBackgroundImg)
                    lockOrUnlockBinding.ivLargeBackground.visibility = View.VISIBLE
                }
                else{
                    lockOrUnlockBinding.ivLargeBackground.visibility = View.INVISIBLE
                }
                if(it.isComic){
                    lockOrUnlockBinding.tvTitle.visibility = View.INVISIBLE
                    lockOrUnlockBinding.titleImage.visibility = View.INVISIBLE
                    lockOrUnlockBinding.tvDetails.visibility = View.INVISIBLE
                    lockOrUnlockBinding.ivComicTitle.visibility = View.VISIBLE
                    lockOrUnlockBinding.tvComicTitle.visibility = View.VISIBLE
                    lockOrUnlockBinding.tvComicTitle.text = it.title
                }
                else{
                    lockOrUnlockBinding.tvTitle.visibility = View.VISIBLE
                    lockOrUnlockBinding.titleImage.visibility = View.VISIBLE
                    lockOrUnlockBinding.tvDetails.visibility = View.VISIBLE
                    lockOrUnlockBinding.ivComicTitle.visibility = View.INVISIBLE
                    lockOrUnlockBinding.tvComicTitle.visibility = View.INVISIBLE
                    lockOrUnlockBinding.tvTitle.text = it.title
                    lockOrUnlockBinding.tvDetails.text = it.longDescription
                }

                if (it.hasLockIcon) {
                    lockOrUnlockBinding.btStart.background = ContextCompat.getDrawable(this,R.drawable.gray_shadow_button)
                    lockOrUnlockBinding.btStart.isClickable = false
                    lockOrUnlockBinding.btStart.setTextColor(R.color.gray)
                    lockOrUnlockBinding.ivLock.visibility = View.VISIBLE
                } else if (it.hasComingSoonState) {
                    lockOrUnlockBinding.btStart.text = "Coming Soon"
                    lockOrUnlockBinding.btStart.background = ContextCompat.getDrawable(this, R.drawable.gray_shadow_button)
                    lockOrUnlockBinding.ivLock.visibility = View.INVISIBLE
                    lockOrUnlockBinding.btStart.isClickable = false
                } else {
                    lockOrUnlockBinding.btStart.updateLayoutParams {
                        this.width = ViewGroup.LayoutParams.MATCH_PARENT
                    }


                    lockOrUnlockBinding.btStart.isClickable = true
                    lockOrUnlockBinding.ivLock.visibility = View.GONE

                    val sessionTitle = it.title

                    lockOrUnlockBinding.btStart.setOnTouchListener { _, motionEvent: MotionEvent ->
                        when (motionEvent.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                lockOrUnlockBinding.btStart.setBackgroundResource(R.drawable.no_shadow_bg_green)
                                val params =
                                    lockOrUnlockBinding.btStart.layoutParams as ViewGroup.MarginLayoutParams
                                params.bottomMargin = params.bottomMargin - 15
                                lockOrUnlockBinding.btStart.layoutParams = params
                            }

                            MotionEvent.ACTION_UP -> {
                                lockOrUnlockBinding.btStart.setBackgroundResource(R.drawable.shadow_button_bg)
                                val params =
                                    lockOrUnlockBinding.btStart.layoutParams as ViewGroup.MarginLayoutParams
                                params.bottomMargin = params.bottomMargin + 15
                                lockOrUnlockBinding.btStart.layoutParams = params
                            }
                        }
                        false
                    }
                    lockOrUnlockBinding.btStart.setOnClickListener {


                        lockOrUnlockViewModel.heartFlow.observe(this){
                            if (it.equals(0)){
                               showBadHearts()
                            }
                            else{
                                Firebase.analytics.logEvent("simm_session_started") {
                                    param(FirebaseAnalytics.Param.ITEM_NAME, sessionTitle)
                                }

                                startActivity(Intent(this@LockOrUnlockActivity, SplashScreen::class.java))
                                finish()
                            }
                        }




                    }
                }
                if (it.titleAnimation > 0) {
                    lockOrUnlockBinding.ivSession.setAnimation(it.titleAnimation)
                }
                if(it.lockPageCharacter != ""){
                    displayCharacter = it.lockPageCharacter

                    if(displayCharacter == "Grace" || displayCharacter == "grace"){
                        lockOrUnlockBinding.ivSimmMain.visibility = View.INVISIBLE
                        lockOrUnlockBinding.ivSimmMainUnderlay.setImageResource(R.drawable.grace_neutral)
                    }
                    else{
                        lockOrUnlockBinding.ivSimmMain.visibility = View.VISIBLE
                        lockOrUnlockBinding.ivSimmMainUnderlay.setImageResource(R.drawable.neutral_mouth_closed_floating)
                    }
                }

                Firebase.analytics.logEvent("simm_title_screen_started") {
                    param(FirebaseAnalytics.Param.ITEM_NAME, it.title)
                }
            }
        }


        val appContext = Config.instance.applicationContext!!
        val glanceId = appContext.resources.getIdentifier("simm_blink_glance", "raw", appContext.packageName)
        val blinkId = appContext.resources.getIdentifier("simm_idleblink", "raw", appContext.packageName)
        lockOrUnlockBinding.ivSimmMain.addAnimatorListener(object: Animator.AnimatorListener {
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
        lockOrUnlockBinding.ivSimmMain.playAnimation()
    }

    private fun handleSimmAnimation(glanceId: Int, blinkId: Int) {
        val random = Random.Default.nextInt(10)
        if (random < 4) {
            // Glance
            lockOrUnlockBinding.ivSimmMain.setAnimation(glanceId)
            Log.i(TAG, "playing Simm glance animation [$random]")
        }
        else {
            // Blink
            lockOrUnlockBinding.ivSimmMain.setAnimation(blinkId)
            Log.i(TAG, "playing Simm idle blink animation [$random]")
        }
        lockOrUnlockBinding.ivSimmMain.playAnimation()
    }

    @SuppressLint("InflateParams")
    private fun showBadHearts() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.hearts_bottom_sheet, null)
        val heartParentLayout = view.findViewById<ConstraintLayout>(R.id.heart_parent_layout)
        dialog.setContentView(view)
        dialog.show()
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        heartParentLayout.setBackgroundResource(R.drawable.round_corners_red)

        val btOk = view.findViewById<Button>(R.id.btOk)

        btOk.setTextColor(ContextCompat.getColor(this, R.color.candy_red))

        btOk.setOnClickListener {

            lifecycleScope.launch(Dispatchers.Main) {
                delay(100)
                dialog.dismiss()
            }
        }
    }
}