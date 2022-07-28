package com.example.simmone.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.simmone.R
import com.example.simmone.databinding.FragmentNotificationBinding
import com.example.simmone.databinding.FragmentOperationBinding
import com.example.simmone.services.MyNotificationManager
import com.example.simmone.services.NotificationManager2
import com.example.simmone.services.NotificationManager3
import com.example.simmone.utils.Constants
import com.example.simmone.view.activities.SessionActivity
import com.example.simmone.viewmodel.SessionViewModel


class FragmentNotification : Fragment() {

    private lateinit var notificationBinding: FragmentNotificationBinding
    private val viewModel: SessionViewModel by activityViewModels()
    var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        notificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)
        val view = notificationBinding.root
        var animation = AnimationUtils.loadAnimation(context as SessionActivity,R.anim.slide_right)

        viewModel.getNotTxt().observe(context as SessionActivity, Observer {
            if(it.isNotEmpty()){
                notificationBinding.tvTxt.text = it
                notificationBinding.progressbar.progress = 100
                if (countDownTimer!= null)
                    countDownTimer!!.cancel()
                countDownTimer = object : CountDownTimer(5000, 50) {

                    override fun onTick(millisUntilFinished: Long) {
                        notificationBinding.progressbar.incrementProgressBy(-1)
                    }

                    override fun onFinish() {
                        if (viewModel.notPage == 3 || viewModel.notPage == 6){
                            notificationBinding.progressbar.visibility = View.GONE
                        }
                        else if (viewModel.notPage<viewModel.notItems.size-1){
                            viewModel.notPage = viewModel.notPage+1
                            viewModel.notTxtLivedata.value = viewModel.notItems[viewModel.notPage]
                            notificationBinding.tvTxt.startAnimation(animation)
//                            operationBinding.tvTxt.text = viewModel.notItems[viewModel.notPage]
                        }
                        else{
                            viewModel.checkForNextQuestion()
                        }
                        Log.e("notPage",viewModel.notPage.toString())
                    }
                }.start()
                when(it){
                    viewModel.notItems[0] -> {
                        notificationBinding.tvTalking.text = "Smiling"
                        notificationBinding.ivCharacter.setImageResource(R.drawable.grin)
                    }
                    viewModel.notItems[1] -> {
                        notificationBinding.tvTalking.text = "Talking"
                        notificationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_open)
                    }
                    viewModel.notItems[2] -> {
                        notificationBinding.tvTalking.text = "Beaming"
                        notificationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_closed)
                    }
                    viewModel.notItems[3] -> {
                        notificationBinding.tvTalking.text = ""
                        notificationBinding.ivCharacter.visibility = View.INVISIBLE
                        Constants.progressSession = viewModel.progressLiveData.value!!
//                        Constants.PAGE_FLAG = viewModel.page
                        val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<NotificationManager2>().build()
                        WorkManager
                            .getInstance(context as SessionActivity)
                            .enqueue(notificationWorker)

                    }
                    viewModel.notItems[4] -> {
                        notificationBinding.progressbar.visibility = View.VISIBLE
                        notificationBinding.tvTxt.startAnimation(animation)
                        notificationBinding.tvTalking.text = "Smiling"
                        notificationBinding.ivCharacter.visibility = View.VISIBLE
                        notificationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_open)
                    }
                    viewModel.notItems[5] -> {
                        notificationBinding.tvTalking.text = "Talking"
                        notificationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_closed)
                    }
                    viewModel.notItems[6] ->{
                        notificationBinding.tvTalking.text = ""
                        notificationBinding.ivCharacter.visibility = View.INVISIBLE
                        val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<NotificationManager3>().build()
                        WorkManager
                            .getInstance(context as SessionActivity)
                            .enqueue(notificationWorker)
                    }
                    viewModel.notItems[7] ->{
                        notificationBinding.tvTalking.text = "Shocked"
                        notificationBinding.ivCharacter.visibility = View.VISIBLE
                    }

                }
            }
        })
        return view
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentNotification().apply {
            }
    }
}