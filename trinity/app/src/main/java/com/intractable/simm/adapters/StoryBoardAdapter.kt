package com.intractable.simm.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.intractable.simm.R

class StoryBoardAdapter(private var storyTitle:List<String>, private var storyImages:List<Int>, private var storyAnimations: List<Int>):
    RecyclerView.Adapter<StoryBoardAdapter.ViewPager2Holder> (){

    inner class ViewPager2Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tv_story_board_title)
        //val itemImage: ImageView = itemView.findViewById(R.id.iv_story_board)
        val animationView:LottieAnimationView = itemView.findViewById(R.id.iv_story_board)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2Holder {

    return ViewPager2Holder(LayoutInflater.from(parent.context).inflate(R.layout.story_board_item,parent,false))
    }
    override fun getItemCount(): Int {
        return storyTitle.size

    }
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewPager2Holder, position: Int) {


        if (storyImages[position]==0)
        {
            holder.animationView.setAnimation(storyAnimations[position])
            Log.d("hello",storyAnimations[position].toString())

        }
        else{
            holder.animationView.setImageResource(storyImages[position])

        }
        holder.itemTitle.text = storyTitle[position]
        //holder.animationView.setAnimation(R.raw.notification_one)
        //holder.itemImage.setImageResource(storyImages[position])
    }
}