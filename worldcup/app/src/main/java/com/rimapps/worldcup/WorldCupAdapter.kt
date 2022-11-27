package com.rimapps.worldcup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rimapps.worldcup.WorldCupAdapter.*
import com.rimapps.worldcup.databinding.WorldCupBinding

class WorldCupAdapter(
    private val context: Context,
    private val cuplist: ArrayList<Worldcup>
    ): RecyclerView.Adapter<WorldCupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldCupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val worldCupBinding: WorldCupBinding = DataBindingUtil.inflate(layoutInflater,R.layout.cup_list,parent,false)
        return WorldCupViewHolder(worldCupBinding)
    }
    override fun getItemCount(): Int {
        return cuplist.size
    }


    override fun onBindViewHolder(holder: WorldCupViewHolder, position: Int) {
        val worldCupBinding = cuplist[position]
        holder.bind(worldCupBinding)
    }
    class WorldCupViewHolder(
        private val worldCupBinding: WorldCupBinding
    ):RecyclerView.ViewHolder(worldCupBinding.root){
        fun bind(worldcupModel: Worldcup){
            this.worldCupBinding.worldcupModel = worldcupModel
        worldCupBinding.executePendingBindings()
        }

    }


}