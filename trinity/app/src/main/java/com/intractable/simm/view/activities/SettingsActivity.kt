package com.intractable.simm.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.intractable.simm.databinding.ActivitySettingsBinding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.PlantdexViewModel
import com.intractable.simm.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsBinding: ActivitySettingsBinding
    lateinit var appUtil: AppUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(settingsBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()


        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        settingsBinding.swAnalytics.isChecked = settingsViewModel.analyticsEnabled

        settingsViewModel.settingsComment.observe(this) {
            settingsBinding.tvTitle.text = it

            lifecycleScope.launch(Dispatchers.Main) {
                delay(1000)

                settingsBinding.tvTitle.fadeVisibility(View.VISIBLE)
            }

            settingsBinding.ivBackButton.setOnClickListener {
                finish()
            }
            settingsBinding.btPrivacyPolicy.setOnClickListener {
                val i = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://intractableltd.com/privacy.html ")
                )
                startActivity(i)
            }

            settingsBinding.swAnalytics.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    lifecycleScope.launch {
                        Config.instance.storageManager?.storeAnalyticsStatus(true)
                    }
                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
                    Toast.makeText(this, "Use of analytics turned on", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        Config.instance.storageManager?.storeAnalyticsStatus(false)
                    }
                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
                    Toast.makeText(this, "Use of analytics turned off", Toast.LENGTH_SHORT).show()

                }
            }

            settingsBinding.btDeleteAnalytics.setOnClickListener {
                FirebaseAnalytics.getInstance(this).resetAnalyticsData()
                Toast.makeText(this, "Analytics data deleted", Toast.LENGTH_SHORT).show()
            }

            settingsBinding.btListOfAcknowledgments.setOnClickListener {
                val intent = Intent(this, AcknowledgmentsActivity::class.java)
                startActivity(intent)
            }

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

