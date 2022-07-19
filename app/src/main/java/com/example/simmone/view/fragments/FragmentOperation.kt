package com.example.simmone.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.simmone.R
import com.example.simmone.databinding.FragmentMcqBinding
import com.example.simmone.databinding.FragmentOperationBinding
import com.example.simmone.services.MyNotificationManager
import com.example.simmone.utils.Constants
import com.example.simmone.view.activities.SessionActivity
import com.example.simmone.viewmodel.SessionViewModel


class FragmentOperation : Fragment() {
    private lateinit var operationBinding: FragmentOperationBinding
    private val viewModel: SessionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        operationBinding = FragmentOperationBinding.inflate(inflater, container, false)
        val view = operationBinding.root

        viewModel.getNotTxt().observe(context as SessionActivity, Observer {
            if(it.isNotEmpty()){
                operationBinding.tvTxt.text = it
                object : CountDownTimer(6000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        operationBinding.tvTimer.setText((millisUntilFinished / 1000 ).toString()+ " seconds remaining")
                    }

                    override fun onFinish() {
                        if (viewModel.notPage == 4){
                            operationBinding.tvTimer.visibility = View.GONE
                            operationBinding.progressbar.visibility = View.GONE
                        }
                        else{
                            if (viewModel.notPage == 2)
                                operationBinding.tvTxt.setTextColor(Color.parseColor("#5AB0FF"))
                            else
                                operationBinding.tvTxt.setTextColor(Color.parseColor("#E6E6E6"))
                            viewModel.notPage = viewModel.notPage+1
                            viewModel.notTxtLivedata.value = viewModel.notItems[viewModel.notPage]
                            operationBinding.tvTxt.text = viewModel.notItems[viewModel.notPage]
                        }
                        Log.e("notPage",viewModel.notPage.toString())
                    }
                }.start()
                when(it){
                    viewModel.notItems[0] -> {
                        operationBinding.tvTalking.visibility = View.VISIBLE
                    }
                    viewModel.notItems[1] -> {
                        operationBinding.tvTalking.visibility = View.GONE
                        operationBinding.tvTalking2.visibility = View.VISIBLE
                    }
                    viewModel.notItems[2] -> {
                        operationBinding.tvTalking2.visibility = View.GONE
                        operationBinding.tvBeep.visibility = View.VISIBLE
                    }
                    viewModel.notItems[3] -> {
                        operationBinding.tvBeep.visibility = View.GONE
                        operationBinding.tvTalking3.visibility = View.VISIBLE
                    }
                    viewModel.notItems[4] -> {
                        operationBinding.tvTalking3.visibility = View.GONE
                        operationBinding.tvTalking.visibility = View.VISIBLE
                        Constants.PAGE_FLAG = viewModel.page
                        val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<MyNotificationManager>().build()
                        WorkManager
                            .getInstance(context as SessionActivity)
                            .enqueue(notificationWorker)
                    }
                    viewModel.notItems[5] -> {
                        operationBinding.tvTalking.visibility = View.GONE
                        operationBinding.tvSmile.visibility = View.VISIBLE
                        operationBinding.btnContinue.visibility = View.VISIBLE
                    }
                }
            }
        })
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentOperation().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
