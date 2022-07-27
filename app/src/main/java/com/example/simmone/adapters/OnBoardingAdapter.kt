package com.example.simmone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simmone.R
import com.example.simmone.databinding.OnBoardingBinding
import com.example.simmone.model.OnBoarding


class OnBoardingAdapter(
    private val onBoardingList: ArrayList<OnBoarding>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):OnBoardingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val onBoardingBinding: OnBoardingBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.onboarding_list, parent, false)
        return OnBoardingViewHolder(onBoardingBinding)
    }

    override fun getItemCount(): Int {
        return onBoardingList.size
    }

    fun getItemList() : ArrayList<OnBoarding>{
        return onBoardingList;
    }
    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        val onBoardingViewModel = onBoardingList[position]
        holder.bind(onBoardingViewModel)
    }

    class OnBoardingViewHolder(
        private val onBoardingBinding: OnBoardingBinding,

    ) : RecyclerView.ViewHolder(onBoardingBinding.root) {

        fun bind(onBoardingViewModel: OnBoarding) {
            this.onBoardingBinding.onboardModel = onBoardingViewModel
            onBoardingBinding.executePendingBindings()
        }
    }
}