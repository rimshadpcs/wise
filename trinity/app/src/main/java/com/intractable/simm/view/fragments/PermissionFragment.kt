package com.intractable.simm.view.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PermissionFragment:FragmentOperation() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    private val viewModel: SessionViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.hintFlow.observe(context as SessionActivity){
            if (it != null){
                changeTheText(it)
            lifecycleScope.launch {
                delay(2000)
                operationBinding.btContinue.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun startData() {
        viewModel.startOperationData()
    }

    override fun doAction() {
        lifecycleScope.launch {
            delay(5000)
            checkForPermission(android.Manifest.permission.CAMERA,"camera",SessionViewModel.REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }




    private fun checkForPermission(permission: String, name: String,requestCode: Int ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) -> {
                    Log.i("FragmentPermissions", "permission granted")
                    viewModel.progressManager.updateState("case 1")
                }
                else -> ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }
}