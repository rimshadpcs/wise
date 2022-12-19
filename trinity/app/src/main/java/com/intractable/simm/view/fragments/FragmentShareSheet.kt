package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.intractable.simm.viewmodel.SessionViewModel
import com.intractable.simm.view.activities.TextShareActivity


class FragmentShareSheet: FragmentOperation() {


    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    var flag =0
    var name = "Simm"

    object TextShareBroadcast : BroadcastReceiver() {

        var componentName = ""
        @SuppressLint("NewApi", "SuspiciousIndentation")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)

        override fun onReceive(context: Context, intent: Intent?) {
            context.unregisterReceiver(this)

            componentName =
                intent?.getParcelableExtra<ComponentName>(Intent.EXTRA_CHOSEN_COMPONENT).toString()
            Toast.makeText(context,componentName, Toast.LENGTH_SHORT).show()
            Log.e("component", componentName)

        }
    }

    private val viewModel: SessionViewModel by activityViewModels()
    override fun startData() {
        viewModel.startOperationData()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun doAction() {

        changeTheText("")

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, "Learn all about your phone with the Simm app!")
        intent.type = "text/plain"

        val shareAction = Intent.ACTION_SEND
        val receiver = Intent(shareAction)
        receiver.putExtra("test", "test")
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(requireActivity(), 0, receiver, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(requireActivity(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        val chooser = Intent.createChooser(intent, "test", pendingIntent.intentSender)


        requireActivity().registerReceiver(TextShareBroadcast, IntentFilter(shareAction))
        flag = 1
        startActivity(chooser)
        super.onPause()
    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }



    override fun onResume() {
        super.onResume()
        if (flag == 1) {

            if (TextShareBroadcast.componentName.isEmpty()) {
                changeTheText(viewModel.operationDataLiveData.value?.textError)
                operationBinding.btContinue.visibility = View.VISIBLE
            }
            else{
                changeTheText(viewModel.operationDataLiveData.value?.text)
                operationBinding.btContinue.visibility = View.VISIBLE
            }

        }

        }
    }

