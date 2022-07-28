package com.intractable.simm.view.fragments

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
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentOperationBinding
import com.intractable.simm.services.MyNotificationManager
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class FragmentOperation : Fragment() {
    private lateinit var operationBinding: FragmentOperationBinding
    private val viewModel: SessionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        operationBinding = FragmentOperationBinding.inflate(inflater, container, false)
        val view = operationBinding.root
        var animation = AnimationUtils.loadAnimation(context as SessionActivity,R.anim.slide_right)

        viewModel.getNotTxt().observe(context as SessionActivity, Observer {
            if(it.isNotEmpty()){
                operationBinding.tvTxt.text = it
                object : CountDownTimer(5000, 50) {

                    override fun onTick(millisUntilFinished: Long) {
//                        operationBinding.tvTimer.setText((millisUntilFinished / 1000 ).toString()+ " seconds remaining")
                        operationBinding.progressbar.incrementProgressBy(-1)
                    }

                    override fun onFinish() {
                        operationBinding.progressbar.setProgress(100)
                        if (viewModel.notPage == 4){
//                            operationBinding.tvTimer.visibility = View.GONE
                            operationBinding.progressbar.visibility = View.GONE
                        }
                        else{
                            if (viewModel.notPage == 2)
                                operationBinding.tvTxt.setTextColor(Color.parseColor("#5AB0FF"))
                            else
                                operationBinding.tvTxt.setTextColor(Color.parseColor("#E6E6E6"))
                            viewModel.notPage = viewModel.notPage+1
                            viewModel.notTxtLivedata.value = viewModel.notItems[viewModel.notPage]
                            operationBinding.tvTxt.startAnimation(animation)
//                            operationBinding.tvTxt.text = viewModel.notItems[viewModel.notPage]
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
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_closed)
                    }
                    viewModel.notItems[2] -> {
                        operationBinding.tvTalking2.visibility = View.GONE
                        operationBinding.tvBeep.visibility = View.VISIBLE
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_open)
                    }
                    viewModel.notItems[3] -> {
                        operationBinding.tvBeep.visibility = View.GONE
                        operationBinding.tvTalking3.visibility = View.VISIBLE
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_closed)
                    }
                    viewModel.notItems[4] -> {
                        operationBinding.tvTalking3.visibility = View.GONE
                        operationBinding.tvTalking.visibility = View.VISIBLE
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_open)
                        Constants.PAGE_FLAG = viewModel.page
                        Constants.progressSession = viewModel.progressLiveData.value!!
                        val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<MyNotificationManager>().build()
                        WorkManager
                            .getInstance(context as SessionActivity)
                            .enqueue(notificationWorker)
                    }
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
