package com.intractable.simm.view.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentOperationBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class FragmentPermissions : Fragment() {
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

        viewModel.getPermissionTxt().observe(context as SessionActivity, Observer {
            if(it.isNotEmpty()){
                operationBinding.tvTxt.text = it
                object : CountDownTimer(5000, 50) {

                    override fun onTick(millisUntilFinished: Long) {
//                        operationBinding.tvTimer.setText((millisUntilFinished / 1000 ).toString()+ " seconds remaining")
                        operationBinding.progressbar.incrementProgressBy(-1)
                    }

                    override fun onFinish() {
                        operationBinding.progressbar.setProgress(100)
                        if (viewModel.permissionPage == 2){
//                            operationBinding.tvTimer.visibility = View.GONE
                            operationBinding.progressbar.visibility = View.GONE
                            viewModel.checkForNextQuestion()
                        }
                        else if (viewModel.permissionPage == 1) {
                            Log.i("FragmentPermissions", "Waiting for user permission")
                            checkForPermission(android.Manifest.permission.CAMERA,"camera", 101)
                        }
                        else if (viewModel.permissionPage == 0) {
                            viewModel.permissionPage = viewModel.permissionPage+1
                            viewModel.permissionTxtLivedata.value = viewModel.permissionItems[viewModel.permissionPage]
                            operationBinding.tvTxt.startAnimation(animation)
                        }
                        Log.e("permissionPage",viewModel.permissionPage.toString())
                    }
                }.start()
                when(it){
                    viewModel.permissionItems[0] -> {
                        operationBinding.tvTalking.visibility = View.VISIBLE
                    }
                    viewModel.permissionItems[1] -> {
                        operationBinding.tvTalking.visibility = View.GONE
                        operationBinding.tvTalking2.visibility = View.VISIBLE
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_closed)
                    }
                    viewModel.permissionItems[2] -> {
                        operationBinding.tvTalking2.visibility = View.GONE
                        operationBinding.tvBeep.visibility = View.VISIBLE
                        operationBinding.ivCharacter.setImageResource(R.drawable.neutral_mouth_open)
                    }
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkForPermission(permission: String, name: String,requestCode: Int ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    context!!,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.i("FragmentPermissions", "permission granted")
                }
                else -> ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
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
