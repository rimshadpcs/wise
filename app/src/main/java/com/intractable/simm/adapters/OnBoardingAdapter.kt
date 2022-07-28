package com.intractable.simm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intractable.simm.R
import com.intractable.simm.databinding.OnBoardingBinding
import com.intractable.simm.model.OnBoarding


class OnBoardingAdapter(
    private val onBoardingList: ArrayList<OnBoarding>) :
    RecyclerView.Adapter<com.intractable.simm.adapters.OnBoardingAdapter.OnBoardingViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):OnBoardingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val onBoardingBinding: OnBoardingBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.onboarding_list, parent, false)
        return OnBoardingViewHolder(
            onBoardingBinding
        )
    }

    override fun getItemCount(): Int {
        return onBoardingList.size
    }

    fun getItemList() : ArrayList<OnBoarding>{
        return onBoardingList;
    }
    override fun onBindViewHolder(holder:OnBoardingViewHolder, position: Int) {
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