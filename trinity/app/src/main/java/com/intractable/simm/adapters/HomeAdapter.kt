package com.intractable.simm.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.databinding.HomeBinding
import com.intractable.simm.model.SessionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeAdapter(
    private val context: Context,
    val lessonList: ArrayList<SessionModel>,
    val layoutManager: LinearLayoutManager,
    val recyclerView: RecyclerView) :

    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var isExpanded = false
    private var dListener: DataPassInterface?=null
    private val milliSecondsPerInch = 2500f
    interface DataPassInterface{
        fun passData(homeItems: SessionModel, homeBinding: HomeBinding, layoutPosition: Int, list: ArrayList<SessionModel>)
        fun scrollToPosition(position: Int)
    }



    fun setDataToPass(listener:DataPassInterface){
        dListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
            val homeBinding: HomeBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.session_items, parent, false)
            return HomeViewHolder(homeBinding, dListener)
//        }
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }
    fun getItemList() : ArrayList<SessionModel>{
        return lessonList
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder:HomeViewHolder, layoutPosition: Int) {
        val model: SessionModel = lessonList[layoutPosition]
        holder.homeBinding.homeModel = lessonList[layoutPosition]
        val homeViewModel = lessonList[layoutPosition]
        holder.bind(homeViewModel)
        val dpiScale: Float = context.resources.displayMetrics.density


        resetViews(holder)

        if(homeViewModel.isComic || homeViewModel.isGame){
            // height of elements are determined by the height + margin of the largest object (tv_comic_name) which is 56 + 2*2

            // set view height
            val params : ViewGroup.LayoutParams = holder.homeBinding.comicContainer.layoutParams
            params.height = (60 * 2.5 * dpiScale + 0.5f).toInt()  // https://developer.android.com/training/multiscreen/screendensities#TaskUseDP
            holder.homeBinding.comicContainer.layoutParams = params

            // set expandable layout height
            val expandableParams : ViewGroup.LayoutParams = holder.homeBinding.expandableLayout.layoutParams
            expandableParams.height = (60 * 2.5 * dpiScale + 0.5f).toInt()  // https://developer.android.com/training/multiscreen/screendensities#TaskUseDP
            holder.homeBinding.expandableLayout.layoutParams = expandableParams

            // set lock icon and text to left center
            holder.homeBinding.tvLessonDescription.gravity = Gravity.CENTER_VERTICAL

            // show/hide texts and icons
            holder.homeBinding.mainLayout.visibility = View.GONE
            holder.homeBinding.comicContainer.visibility = View.VISIBLE

            // show next arrow for comic
            holder.homeBinding.ivNextArrow.visibility = View.VISIBLE

            // make lock icon bigger for comic
            holder.homeBinding.tvLessonDescription.textSize = 20f

            if(homeViewModel.isGame){
                holder.homeBinding.tvHighscore.visibility = View.VISIBLE
            }
        }
        else{


        }

        if(homeViewModel.hasBackgroundImg){
            holder.homeBinding.mainLayout.background = AppCompatResources.getDrawable(context, homeViewModel.backgroundImg)
            holder.homeBinding.comicContainer.background = AppCompatResources.getDrawable(context, homeViewModel.backgroundImg)
        }
        else{
            holder.homeBinding.mainLayout.setBackgroundColor(Color.parseColor("#FF1D1D1D"))
            holder.homeBinding.comicContainer.setBackgroundColor(Color.parseColor("#FF1D1D1D"))
        }

        if(homeViewModel.hasLowerBackgroundImg){
            holder.homeBinding.expandableLayout.background = AppCompatResources.getDrawable(context, homeViewModel.lowerBackgroundImg)
            holder.homeBinding.tvLessonDescription.setBackgroundColor(Color.parseColor("#00000000"))
            val paddingSize = (8 * dpiScale + 0.5f).toInt()
            holder.homeBinding.tvLessonDescription.setPadding(paddingSize, paddingSize, paddingSize, paddingSize)  // to algin it with the text in the upper box
        }
        else{
            holder.homeBinding.expandableLayout.setBackgroundColor(Color.parseColor("#FF333333"))
            holder.homeBinding.tvLessonDescription.background = AppCompatResources.getDrawable(context, R.drawable.chip_home)
            val paddingSize = (16 * dpiScale + 0.5f).toInt()
            holder.homeBinding.tvLessonDescription.setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
        }


        if (homeViewModel.hasPlayIcon) {
            if(homeViewModel.isComic){
                holder.homeBinding.ivComicStatusIcon.setImageResource(R.drawable.play_icon)
                holder.homeBinding.ivComicStatusIcon.visibility = View.VISIBLE
            }
            else {
                holder.homeBinding.ivLessonStatusIcon.setImageResource(R.drawable.play_icon)
                holder.homeBinding.ivLessonStatusIcon.visibility = View.VISIBLE
            }
            holder.homeBinding.tvLessonDescription.text = homeViewModel.description
        }

        if (homeViewModel.hasCheckMark) {
            if(homeViewModel.isComic){
                holder.homeBinding.ivComicStatusIcon.setImageResource(R.drawable.tick_icon)
                holder.homeBinding.ivComicStatusIcon.visibility = View.VISIBLE
            }
            else {
                holder.homeBinding.ivLessonStatusIcon.setImageResource(R.drawable.tick_icon)
                holder.homeBinding.ivLessonStatusIcon.visibility = View.VISIBLE
            }
            holder.homeBinding.tvLessonDescription.text = homeViewModel.description
        }
        if(homeViewModel.completionStatus == StorageManager.SessionStatus.AttemptedIncomplete){
            holder.homeBinding.ivLessonStatusIcon.setImageResource(R.drawable.dash_icon)
        }


        if (homeViewModel.hasLockIcon) {

            val descriptionText = SpannableString("     "+homeViewModel.description)

            val lockEmoji = ContextCompat.getDrawable(context, R.drawable.lock)
            val fontMetrics = holder.homeBinding.tvLessonDescription.paint.fontMetrics
            val lockHeight = ((-1 * fontMetrics.ascent + fontMetrics.descent) * 0.8).toInt()
            val lockWidth = ((lockEmoji!!.intrinsicWidth.toFloat() / lockEmoji.intrinsicHeight.toFloat()) * lockHeight).toInt()
            lockEmoji.setBounds(0, 0, lockWidth, lockHeight)

            val span = ImageSpan(lockEmoji, ImageSpan.ALIGN_CENTER)
            descriptionText.setSpan(span, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            holder.homeBinding.tvLessonDescription.text = descriptionText
        }

        val isExpandable: Boolean = lessonList[layoutPosition].expandable

        if (!isExpandable) {
            holder.homeBinding.expandableLayout.visibility = View.GONE
        }
        else
        {
            holder.homeBinding.expandableLayout.visibility = View.VISIBLE
        }


        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return milliSecondsPerInch / recyclerView.computeVerticalScrollRange()

                }
            }

        holder.homeBinding.mainLayout.setOnClickListener {

            val item = lessonList[layoutPosition]
            item.expandable = !model.expandable

            if (layoutPosition!=0 && holder.homeBinding.expandableLayout.visibility==View.GONE) {
                recyclerView.apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(350)
                        linearSmoothScroller.targetPosition = (layoutPosition)
                        (layoutManager as LinearLayoutManager).startSmoothScroll(
                            linearSmoothScroller
                        )
                    }
                }
            }
            notifyItemChanged(layoutPosition)
        }
        holder.homeBinding.comicContainer.setOnClickListener {
            val item = lessonList[layoutPosition]
            item.expandable = !model.expandable
            if (layoutPosition!=0 && holder.homeBinding.expandableLayout.visibility==View.GONE) {
                CoroutineScope(Dispatchers.Main).launch{
                    delay(350)
                recyclerView.apply {
                    linearSmoothScroller.targetPosition = (layoutPosition)
                    (layoutManager as LinearLayoutManager).startSmoothScroll(linearSmoothScroller)

                }
                }
            }

            notifyItemChanged(layoutPosition)
        }
    }

    private fun resetViews(holder: HomeViewHolder) {
        holder.homeBinding.lessonAccessLayout.visibility = View.GONE
        holder.homeBinding.lessonAccessLayoutExtra.visibility = View.GONE
        holder.homeBinding.ivLessonStatusIcon.visibility = View.INVISIBLE
        holder.homeBinding.ivComicStatusIcon.visibility = View.INVISIBLE

        val params : ViewGroup.LayoutParams = holder.homeBinding.mainLayout.layoutParams
        params.height = (context.resources.getDimension(R.dimen.lesson_title_bar_size) + 0.5f).toInt()  // https://developer.android.com/training/multiscreen/screendensities#TaskUseDP
        holder.homeBinding.mainLayout.layoutParams = params

        // set expandable layout height
        val expandableParams : ViewGroup.LayoutParams = holder.homeBinding.expandableLayout.layoutParams
        expandableParams.height = (context.resources.getDimension(R.dimen.lesson_expandable_bar_size) + 0.5f).toInt()  // https://developer.android.com/training/multiscreen/screendensities#TaskUseDP
        holder.homeBinding.expandableLayout.layoutParams = expandableParams

        // set lock icon and text to left top
        holder.homeBinding.tvLessonDescription.gravity = Gravity.START

        // show/hide texts and icons
        holder.homeBinding.mainLayout.visibility = View.VISIBLE
        holder.homeBinding.comicContainer.visibility = View.GONE

        // hide next arrow for non-comics
        holder.homeBinding.ivNextArrow.visibility = View.GONE

        // make text default size for normal sessions
        holder.homeBinding.tvLessonDescription.textSize = 17f

        // highscore
        holder.homeBinding.tvHighscore.visibility = View.INVISIBLE
    }

    inner class HomeViewHolder(
        val homeBinding: HomeBinding,
        private val listener: HomeAdapter.DataPassInterface?

    ): RecyclerView.ViewHolder(homeBinding.root){
        fun bind(homeViewModel: SessionModel){
            this.homeBinding.homeModel = homeViewModel

            listener?.passData( homeViewModel,homeBinding,layoutPosition, lessonList)

            homeBinding.executePendingBindings()
        }

        fun scrollToLast(position: Int) {
            listener?.scrollToPosition(position)
        }

    }

}


