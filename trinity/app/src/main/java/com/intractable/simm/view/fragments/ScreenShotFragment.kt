package com.intractable.simm.view.fragments

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val READ_EXTERNAL_STORAGE_REQUEST = 154
private const val READ_MEDIA_IMAGES = 155

class ScreenShotFragment : FragmentOperation() {

    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    var flag =0
    var name = "Simm"

    val END_SESSION = 101

    override fun startData(){
        viewModel.startOperationData()
    }


    inner class ScreenShotDetector() {
        var contentObserver: ContentObserver? = null
        fun start() {
            if (contentObserver == null) {
                contentObserver = context?.contentResolver?.registerObserver()
            }
        }
        fun stop() {
            contentObserver?.let { context?.contentResolver?.unregisterContentObserver(it) }
            contentObserver = null
        }
        fun queryScreenshots(uri: Uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                queryRelativeDataColumn(uri)
            } else {
                queryDataColumn(uri)
            }
        }

        fun queryDataColumn(uri: Uri) {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            context?.contentResolver?.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    val path = cursor.getString(dataColumn)
                    if (path.contains("screenshot", true)) {
                        stop()
                        nextPage()
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun queryRelativeDataColumn(uri: Uri) {
            val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH
            )
            context?.contentResolver?.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val relativePathColumn =
                    cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
                val displayNameColumn =
                    cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(displayNameColumn)
                    val relativePath = cursor.getString(relativePathColumn)
                    if (name.contains("screenshot", true) or
                        relativePath.contains("screenshot", true)
                    ) {
                       stop()
                        nextPage()
                    }
                }
            }
        }
        private fun ContentResolver.registerObserver(): ContentObserver {
            val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                    uri?.let { queryScreenshots(it) }
                }
            }
            registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver)
            return contentObserver
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            READ_EXTERNAL_STORAGE_REQUEST ->{
                if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                return
            }
        }
    }

    private val viewModel: SessionViewModel by activityViewModels()

    override fun nextPage() {
        if(flag==END_SESSION){
            EndSession()
        }
        else{
            viewModel.nextOperationPage()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun doAction() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(5000)
            if (flag==0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkForPermission(155)
                }
                else{
                    checkForPermission(154)
                }
                flag = 1
            }
        }

        if(flag==1){
            ScreenShotDetector().start()
        }
        if (flag==3){
            Toast.makeText(context, flag.toString(), Toast.LENGTH_LONG).show()
            viewModel.nextOperationPage()
        }
//        else{
//            flag = END_SESSION
//            changeTheText(viewModel.operationDataLiveData.value?.textError)
//            operationBinding.btContinue.visibility=View.VISIBLE
//        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkForPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= 32) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) -> {
                    nextPage()
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                }
            }

        } else {

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    nextPage()
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                nextPage()
            }
            else {
                showSkipButton()
            }
        }


    }


