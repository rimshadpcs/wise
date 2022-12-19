package com.intractable.simm.view.fragments

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.intractable.simm.utils.Constants
import com.intractable.simm.viewmodel.SessionViewModel

class WidgetFragment:FragmentOperation() {
    private val viewModel: SessionViewModel by activityViewModels()

    private var RETRY_FLAG = 0

    override val eventPrefix: String
        get() {
            return "simm_" + this.javaClass.simpleName
        }


    override fun startData() {
        viewModel.startOperationData()
    }

    override fun doAction() {

    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }

    override fun editText() {
        // not needed
    }

    override fun onResume() {
        super.onResume()

        val operationData = viewModel.operationDataLiveData.value
        if (operationData != null && operationData.shouldDoAction && Constants.WidgetsInstalled) {
            Constants.WidgetsInstalled = false
            viewModel.nextOperationPage()
        }
        if (RETRY_FLAG == 2)
            showSkipButton()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.operationDataLiveData.value!!.shouldDoAction)
            RETRY_FLAG += 1
    }
}