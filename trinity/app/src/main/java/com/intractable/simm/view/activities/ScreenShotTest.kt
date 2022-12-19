package com.intractable.simm.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intractable.simm.databinding.ActivityScreenShotTestBinding
import com.intractable.simm.services.ScreenshotDetector


const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
@SuppressLint("StaticFieldLeak")
private lateinit var screenShotTestDetector: ScreenshotDetector

class ScreenShotTest : AppCompatActivity() {

    private lateinit var binding:  ActivityScreenShotTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenShotTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenShotTestDetector = ScreenshotDetector(baseContext)


        binding.log.setOnClickListener {
            Toast.makeText(this, "clicked",Toast.LENGTH_SHORT).show()
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("content://call_log/calls"))
            startActivity(i)

        }
        binding.dialer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:")
            startActivity(intent)


        }
    }



    override fun onStart() {
        super.onStart()
        detectScreenShot()
    }

    override fun onStop() {
        super.onStop()
        screenShotTestDetector.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            READ_EXTERNAL_STORAGE_REQUEST->{
                if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    detectScreenShot()
                }
                return
            }
        }
    }
    private fun haveStoragePermission()=
        ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun detectScreenShot() {


        if(haveStoragePermission()){
            screenShotTestDetector.start()
        }
        else{
            requestPermission()
        }
    }

}
