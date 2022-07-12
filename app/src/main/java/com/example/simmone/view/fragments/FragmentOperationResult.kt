package com.example.simmone.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.simmone.databinding.FragmentOperationResultBinding
import com.example.simmone.utils.Constants
import com.example.simmone.view.activities.SessionActivity
import com.example.simmone.viewmodel.SessionViewModel
import javax.inject.Inject
import javax.inject.Named

class FragmentOperationResult : Fragment() {

    @Inject
    @Named("outputMessageForNotification")
    lateinit var outputMessageForNotification: String
    @Inject
    @Named("nextButton")
    lateinit var nextButton: String

    private lateinit var operationResultBinding: FragmentOperationResultBinding
    private  val viewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        operationResultBinding = FragmentOperationResultBinding.inflate(inflater, container, false)
        val view = operationResultBinding.root

        operationResultBinding.tvResult.text = outputMessageForNotification
        operationResultBinding.btNext.text = nextButton

        operationResultBinding.btNext.setOnClickListener {
            val intent = Intent(this as SessionActivity, McqActivity::class.java)
            intent.putExtra("FROM", Constants.FLAG_OPERATION_RESULT)
            startActivity(intent)
            finish()
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentOperationResult().apply {
                arguments = Bundle().apply {
                }
            }
    }
}