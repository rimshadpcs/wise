package com.intractable.simm.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.nearby.connection.Payload
import com.intractable.simm.R
import com.intractable.simm.view.activities.SocialActivity
import com.intractable.simm.viewmodel.SessionViewModel
import com.intractable.simm.viewmodel.SocialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

open class FragmentNearBy:FragmentOperation() {
    private val viewModel: SessionViewModel by activityViewModels()
    var flag = 0
    var resultLauncher:ActivityResultLauncher<Intent>? = null


    companion object
    {
        var REQUIRED_PERMISSIONS: Array<String>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getEvent().observe(requireActivity()) {
            if (it == SessionViewModel.EVENT_CHECK_NEARBY_PERMISSION){
                if (hasPermissions(requireContext(),getRequiredPermissions()))
                    nextPage()
                else
                    showSkipButton()
            }
        }

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                    nextPage()
            }
        }
    }

    override fun startData() {
        viewModel.startOperationData()
    }

    override fun doAction() {
            lifecycleScope.launch {
                delay(5000)
                if (flag == 0 ) {
                    flag = 1
                    if (!hasPermissions(requireContext(), getRequiredPermissions())) {
                        getRequiredPermissions()?.let {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                it,
                                SessionViewModel.REQUEST_CODE_PERMISSIONS_NEARBY
                            )
                        }
                    }else{
                        nextPage()
                    }
                }else{
                    val intent = Intent(requireActivity(),SocialActivity::class.java)
                    intent.putExtra("data","Session")
                    resultLauncher?.launch(intent)

                }
            }

    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }


    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }

    fun hasPermissions(context: Context?, permissions:Array<String>?): Boolean {
        for (permission in permissions!!) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    protected fun getRequiredPermissions(): Array<String>? {
        return REQUIRED_PERMISSIONS
    }
}