package com.intractable.simm.view.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.telecom.Call
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Long
import java.util.*
import kotlin.collections.ArrayList

private const val READ_CALL_LOG = 131

class CallFragment: FragmentOperation() {


    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }

    var flag = 0
    val END_SESSION = 101


    override fun startData() {
            viewModel.startOperationData()
    }



    override fun onResume() {
        super.onResume()
        if (flag==2){
            getCallLogs()

            lifecycleScope.launch(Dispatchers.Main){
                delay(2000)
                operationBinding.btContinue.visibility = View.VISIBLE
            }
        }

    }


    @SuppressLint("SetTextI18n")
    fun getCallLogs() {
//       title.setText(Html.fromHtml("<b>Call Logs</b>"))
        //deviceDetails.setText(Html.fromHtml(""))
        val callLogs = StringBuilder()
        val calllogsBuffer = ArrayList<String>()
        calllogsBuffer.clear()
        val managedCursor: Cursor = requireActivity().managedQuery(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )
        val name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)

        while (managedCursor.moveToNext()) {
            val phName = managedCursor.getString(name)
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getString(date)
            val callDayTime = Date(Long.valueOf(callDate))
            val callDuration = managedCursor.getString(duration)
            var dir: String? = null
            when (callType.toInt()) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }
            calllogsBuffer.add(
                """
            Cached Name: $phName
            Phone Number: $phNumber 
            Call Type: $dir 
            Call Date: $callDayTime 
            Call duration in sec : $callDuration
            
            """
            )

            lifecycleScope.launch {
                viewModel.onlyPhoneFlow.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect {
                    if (phName.isEmpty()) {
                        operationBinding.tvTxt.text = it + phNumber
                    } else if (phName.isNotEmpty() || phName.isNotBlank()) {
                        operationBinding.tvTxt.text = it + phName
                    }
                }
            }

       }
        managedCursor.close()
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            READ_CALL_LOG ->{
                if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                return
            }
        }
    }
    private val viewModel: SessionViewModel by activityViewModels()

    override fun nextPage() {
        if (flag==END_SESSION) {
            EndSession()
        }
        else{
            viewModel.nextOperationPage()
        }
    }


    override fun doAction() {
        lifecycleScope.launch(Dispatchers.Default) {
            delay(5000)
            if (flag==0){
                checkForPermission(131)
                flag=1
            }

        }
        if (flag==1){
            lifecycleScope.launch(Dispatchers.Default) {
                delay(5000)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("content://call_log/calls"))
                startActivity(i)
                flag = 2
            }
        }
    }
    private fun checkForPermission(requestCode: Int) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALL_LOG
            ) -> {
                nextPage()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CALL_LOG)
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