package com.intractable.simm.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.intractable.simm.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsBinding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(settingsBinding.root)


        settingsBinding.btPrivacyPolicy.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://intractableltd.com/privacy.html "))
            startActivity(i)
        }

        val isChecked=
        settingsBinding.swAnalytics.setOnCheckedChangeListener{
            _, isChecked->
            if(isChecked==true){
                Toast.makeText(this, "Use of analytics turned on",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Use of analytics turned off",Toast.LENGTH_SHORT).show()

            }
        }

        settingsBinding.btDeleteAnalytics.setOnClickListener{
            Toast.makeText(this, "Analytics data deleted", Toast.LENGTH_SHORT).show()
        }

        settingsBinding.btListOfAcknowledgments.setOnClickListener {
            val intent = Intent(this, AcknowledgmentsActivity::class.java)
            startActivity(intent)
        }
    }
}