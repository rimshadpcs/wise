package com.example.simmone.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simmone.R
import com.example.simmone.databinding.StatementBinding
import com.example.simmone.databinding.SwapBinding
import com.example.simmone.model.Statement
import com.example.simmone.model.SwappingItems

class SwappingAppsAdapter(
    private val context: Context,
    private val swapAppList: ArrayList<SwappingItems>) :

    RecyclerView.Adapter<SwappingAppsAdapter.SwappingAppViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, swappingItems: SwappingItems)
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwappingAppViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val swapBinding: SwapBinding = DataBindingUtil.inflate(layoutInflater, R.layout.swapping_app_list, parent, false)
        return SwappingAppViewHolder(swapBinding,mListener)
    }

    override fun getItemCount(): Int {
        return swapAppList.size
    }
    fun getItemList() : ArrayList<SwappingItems>{
        return swapAppList;
    }
    override fun onBindViewHolder(holder:SwappingAppViewHolder, position: Int) {
        val swappingAppViewModel = swapAppList[position]
        holder.bind(swappingAppViewModel)
    }

    class SwappingAppViewHolder(
        private val swapBinding: SwapBinding,
        private val listener: OnItemClickListener?

    ): RecyclerView.ViewHolder(swapBinding.root){
        @SuppressLint("ClickableViewAccessibility")
        fun bind(swappingAppViewModel: SwappingItems){
            this.swapBinding.swappingItems = swappingAppViewModel


            itemView.setOnClickListener() {
                listener?.onItemClick(itemView, swappingAppViewModel)
                true
            }


            swapBinding.executePendingBindings()
        }
    }
}


