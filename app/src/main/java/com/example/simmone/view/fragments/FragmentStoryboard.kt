package com.example.simmone.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.simmone.R
import com.example.simmone.adapters.StoryBoardAdapter
import com.example.simmone.databinding.FragmentStoryboardBinding
import com.example.simmone.viewmodel.SessionViewModel

class FragmentStoryboard : Fragment() {
    private lateinit var storyBoardBinding: FragmentStoryboardBinding
    private val sessionViewModel: SessionViewModel by activityViewModels()

    private var storyTitleList = mutableListOf<String>()
    private var storyImageList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storyBoardBinding = FragmentStoryboardBinding.inflate(layoutInflater)
        val view = storyBoardBinding.root

        postToList()

        storyBoardBinding.vpStoryBoard.adapter = StoryBoardAdapter(storyTitleList, storyImageList)
        storyBoardBinding.vpStoryBoard.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val circleIndicator = storyBoardBinding.circleIndicator
        val vpStoryBoard = storyBoardBinding.vpStoryBoard
        circleIndicator.setViewPager2(vpStoryBoard)
        val sizeofList = storyTitleList.size
        storyBoardBinding.btComplete.visibility = View.INVISIBLE

        vpStoryBoard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if (position == sizeofList-1)
                {
                    storyBoardBinding.btComplete.visibility = View.VISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()
                }
                if(storyBoardBinding.btComplete.visibility== View.VISIBLE){
                    storyBoardBinding.btComplete.visibility = View.VISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()

                }

                else{
                    storyBoardBinding.btComplete.visibility = View.INVISIBLE
                    vpStoryBoard.adapter!!.notifyDataSetChanged()
                }
                super.onPageSelected(position)
            }

        })

        storyBoardBinding.btComplete.setOnClickListener {
            sessionViewModel.checkForNextQuestion()
        }

        return view
    }

    private fun addToList(storyTitle: String, storyImage: Int){
        storyTitleList.add(storyTitle)
        storyImageList.add(storyImage)
    }

    private fun postToList() {
        for (storyboardItem in sessionViewModel.storyboardItems) {
            val imageId = activity!!.resources.getIdentifier(storyboardItem.storyBoardImage, "drawable", activity!!.packageName)
            addToList(storyboardItem.storyBoardTitle, imageId)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentStoryboard().apply {
                arguments = Bundle().apply {
                }
            }
    }
}