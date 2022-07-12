package com.example.simmone.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.simmone.R
import com.example.simmone.databinding.ActivityOperationBinding
import com.example.simmone.databinding.FragmentMcqBinding
import com.example.simmone.databinding.FragmentOperationBinding
import com.example.simmone.services.MyNotificationManager
import com.example.simmone.utils.Constants
import com.example.simmone.view.activities.SessionActivity
import com.example.simmone.viewmodel.SessionViewModel


class FragmentOperation : Fragment() {
    private lateinit var operationBinding: FragmentOperationBinding
    private val viewModel: SessionViewModel by viewModels()

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
        operationBinding.btSend.setOnClickListener {
            operationBinding.tvNotificationSend.text = getText(R.string.notificationSentChangeText)
            Constants.PAGE_FLAG = viewModel.page
            val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<MyNotificationManager>().build()
            WorkManager
                .getInstance(context as SessionActivity)
                .enqueue(notificationWorker)
        }
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