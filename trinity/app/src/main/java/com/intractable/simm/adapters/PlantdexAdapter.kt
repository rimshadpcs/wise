package com.intractable.simm.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intractable.simm.R
import com.intractable.simm.databinding.PlantdexBinding
import com.intractable.simm.model.PlantItem

class
PlantdexAdapter(
    private val context: Context,
    private val plantList: ArrayList<PlantItem>
    ):
    RecyclerView.Adapter<PlantdexAdapter.PlantViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)

    val plantdexBinding: PlantdexBinding = DataBindingUtil.inflate(layoutInflater, R.layout.plant_list_items, parent, false)
    return PlantViewHolder(plantdexBinding)

    }
    override fun getItemCount(): Int {
        return plantList.size
    }

    fun getItemList(): ArrayList<PlantItem>{
        return plantList
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val mPlantItem  = plantList[position]
        holder.bind(mPlantItem)
    }

    class PlantViewHolder(
         private val plantdexBinding: PlantdexBinding
    ): RecyclerView.ViewHolder(plantdexBinding.root) {
        fun bind(mPlantItem: PlantItem){
            this.plantdexBinding.plantModel = mPlantItem
            plantdexBinding.executePendingBindings()

        }

    }

}
