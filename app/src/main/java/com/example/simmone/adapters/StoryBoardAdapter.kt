package com.example.simmone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simmone.R
import com.example.simmone.databinding.StatementBinding

class StoryBoardAdapter(private var storyTitle:List<String>, private var storyImages:List<Int>):
    RecyclerView.Adapter<StoryBoardAdapter.ViewPager2Holder> (){

    inner class ViewPager2Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tv_story_board_title)
        val itemImage: ImageView = itemView.findViewById(R.id.iv_story_board)
        

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2Holder {

    return ViewPager2Holder(LayoutInflater.from(parent.context).inflate(R.layout.story_board_item,parent,false))
    }
    override fun getItemCount(): Int {
        return storyTitle.size

    }
    override fun onBindViewHolder(holder: ViewPager2Holder, position: Int) {
        holder.itemTitle.text = storyTitle[position]
        holder.itemImage.setImageResource(storyImages[position])
    }
}