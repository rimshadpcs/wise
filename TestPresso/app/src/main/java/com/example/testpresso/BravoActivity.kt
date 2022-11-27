package com.example.testpresso

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.testpresso.databinding.ActivityBravoBinding

class BravoActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bravoBinding: ActivityBravoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bravoBinding = ActivityBravoBinding.inflate(layoutInflater)
        setContentView(bravoBinding.root)


        bravoBinding.btOk.setOnClickListener {
            finish()
        }
    }
}