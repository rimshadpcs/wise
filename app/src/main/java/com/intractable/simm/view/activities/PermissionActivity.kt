package com.intractable.simm.view.activities

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intractable.simm.databinding.ActivityPermissionBinding
import com.intractable.simm.utils.AppUtil

class PermissionActivity : AppCompatActivity() {
    val cameraRQ = 101

    private lateinit var permissionActivityBinding: ActivityPermissionBinding
    lateinit var appUtil: AppUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionActivityBinding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(permissionActivityBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        clickMeTap()
    }

    private fun clickMeTap(){
        permissionActivityBinding.btClickMe.setOnClickListener{
            checkForPermission(android.Manifest.permission.CAMERA,"camera", cameraRQ)
        }
    }

    private fun checkForPermission(permission: String, name: String,requestCode: Int ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        applicationContext,
                        "$name permission granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )
                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String){
            if(grantResults.isEmpty()|| grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "$name Permission refused",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "$name Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
        when (requestCode){
            cameraRQ->innerCheck("camera")
        }


    }

        private fun showDialog(permission: String,name: String, requestCode: Int ){
            val builder = AlertDialog.Builder(this)

            builder.apply {
                setMessage("Permission to access your $name is required to use this app")
                setTitle("Permission required")
                setPositiveButton("Ok"){dialog, which->
                    ActivityCompat.requestPermissions(this@PermissionActivity, arrayOf(permission),requestCode)

                }
            }
            val dialog = builder.create()
            dialog.show()

    }
}