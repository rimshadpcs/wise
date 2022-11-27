package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.adapters.StoryBoardAdapter
import com.intractable.simm.databinding.FragmentStoryboardBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel

class FragmentStoryboard : Fragment() {
    private lateinit var storyBoardBinding: FragmentStoryboardBinding
    private val sessionViewModel: SessionViewModel by activityViewModels()

    private var storyTitleList = mutableListOf<String>()
    private var storyImageList = mutableListOf<Int>()
    private var storyAnimList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        storyBoardBinding = FragmentStoryboardBinding.inflate(layoutInflater)
        val view = storyBoardBinding.root

        postToList()

        return view
    }

    private fun addToList(storyTitle: String, storyImage: Int, storyBoardAnimation: Int){
        storyTitleList.add(storyTitle)
        storyImageList.add(storyImage)
        storyAnimList.add(storyBoardAnimation)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun postToList() {
        sessionViewModel.storyboardItemsData.observe(context as SessionActivity) {
            val mp : MediaPlayer = MediaPlayer.create(context, R.raw.button_press)
            sessionViewModel.storyboardItems.clear()
            sessionViewModel.storyboardItems.addAll(it)
            for (storyboardItem in sessionViewModel.storyboardItems) {
                addToList(storyboardItem.storyBoardTitle, storyboardItem.storyBoardImage, storyboardItem.storyBoardAnimation)
            }

            val vpStoryBoard = storyBoardBinding.vpStoryBoard
            vpStoryBoard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                @SuppressLint("NotifyDataSetChanged")
                override fun onPageSelected(position: Int) {
                    if (position == storyTitleList.size-1)
                    {
                        storyBoardBinding.btComplete.visibility = View.VISIBLE
                        sessionViewModel.changeProgress()
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

            storyBoardBinding.vpStoryBoard.adapter = StoryBoardAdapter(storyTitleList, storyImageList,storyAnimList)
            storyBoardBinding.vpStoryBoard.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            val circleIndicator = storyBoardBinding.circleIndicator
            circleIndicator.setViewPager2(vpStoryBoard)
            storyBoardBinding.btComplete.visibility = View.INVISIBLE

            storyBoardBinding.btComplete.setOnClickListener {
                if(mp.isPlaying) {
                    mp.pause() // Pause the current track
                }
                mp.start()  // play sound
                sessionViewModel.checkForNextQuestion(true)

                Firebase.analytics.logEvent("simm_storyboard_completed", null)
            }



            storyBoardBinding.btComplete.setOnTouchListener { _, motionEvent: MotionEvent ->
                when (motionEvent.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        storyBoardBinding.btComplete.setBackgroundResource(R.drawable.no_shadow_bg_green)
                        val params =
                            storyBoardBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                        params.bottomMargin = params.bottomMargin - 15
                        storyBoardBinding.btComplete.layoutParams = params
                    }

                    MotionEvent.ACTION_UP -> {
                        storyBoardBinding.btComplete.setBackgroundResource(R.drawable.shadow_button_bg)
                        val params =
                            storyBoardBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                        params.bottomMargin = params.bottomMargin + 15
                        storyBoardBinding.btComplete.layoutParams = params
                    }
                }
                false
            }

            Firebase.analytics.logEvent("simm_storyboard_started", null)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentStoryboard().apply {
                arguments = Bundle().apply {
                }
            }
    }
}