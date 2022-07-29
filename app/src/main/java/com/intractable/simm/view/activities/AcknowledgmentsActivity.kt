package com.intractable.simm.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intractable.simm.R
import com.intractable.simm.utils.AppUtil

class AcknowledgmentsActivity : AppCompatActivity() {
    lateinit var appUtil: AppUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acknowledgments)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()


    }
}