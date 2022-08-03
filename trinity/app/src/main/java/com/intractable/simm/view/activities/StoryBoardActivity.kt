package com.intractable.simm.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.viewpager2.widget.ViewPager2
import com.intractable.simm.R
import com.intractable.simm.adapters.StoryBoardAdapter
import com.intractable.simm.databinding.ActivityStoryBoardBinding
import com.intractable.simm.utils.AppUtil

class StoryBoardActivity : AppCompatActivity() {

    private var storyTitleList = mutableListOf<String>()
    private var storyImageList = mutableListOf<Int>()
    lateinit var appUtil: AppUtil

    private lateinit var storyBoardBinding: ActivityStoryBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storyBoardBinding = ActivityStoryBoardBinding.inflate(layoutInflater)
        setContentView(storyBoardBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        postToList()

        storyBoardBinding.vpStoryBoard.adapter = StoryBoardAdapter(storyTitleList, storyImageList)
        storyBoardBinding.vpStoryBoard.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val circleIndicator = storyBoardBinding.circleIndicator
        val vpStoryBoard = storyBoardBinding.vpStoryBoard
        circleIndicator.setViewPager2(vpStoryBoard)
        val sizeofList = storyTitleList.size
        storyBoardBinding.btComplete.visibility = INVISIBLE


        vpStoryBoard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if (position == sizeofList-1)
                {
                    storyBoardBinding.btComplete.visibility = VISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()
                }
                if(storyBoardBinding.btComplete.visibility== VISIBLE){
                    storyBoardBinding.btComplete.visibility = VISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()

                }

                else{
                    storyBoardBinding.btComplete.visibility = INVISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()
                }
                super.onPageSelected(position)
            }

        })
        storyBoardBinding.btComplete.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
            vpStoryBoard.unregisterOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

            })
        }

        }

    private fun addToList(storyTitle: String, storyImage: Int){
        storyTitleList.add(storyTitle)
        storyImageList.add(storyImage)

    }
    private fun postToList() {
        for (i in 1..5) {
            addToList("Title $i", R.drawable.simm_cover)

        }
    }
}