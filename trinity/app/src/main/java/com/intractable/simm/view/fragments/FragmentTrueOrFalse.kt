package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.media.MediaPlayer
import android.os.*
import android.transition.TransitionInflater
import android.view.*
import android.view.View.DRAG_FLAG_OPAQUE
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.adapters.StatementAdapter
import com.intractable.simm.databinding.FragmentTrueOrFalseBinding
import com.intractable.simm.databinding.StatementBinding
import com.intractable.simm.model.Statement
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentTrueOrFalse : Fragment(),StatementAdapter.OnItemTouchListener {

    private lateinit var trueOrFalseBinding: FragmentTrueOrFalseBinding
    private var statementAdapter: StatementAdapter? = null
    private val sessionViewModel: SessionViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val dragMessage = "Added"
    private lateinit var draggingItem: TextView
    private lateinit var selectedItemModel: Statement
    private var finished = false
    private var errorCount =0
    private var question: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        trueOrFalseBinding = FragmentTrueOrFalseBinding.inflate(inflater, container, false)
        val view = trueOrFalseBinding.root

        sessionViewModel.sortingData.observe(context as SessionActivity) {
            trueOrFalseBinding.tvStatementQuestion.text = it.question
            trueOrFalseBinding.cardTruthBox.text = it.button_left
            trueOrFalseBinding.cardFalseBox.text = it.button_right
            statementAdapter = StatementAdapter(requireActivity(), it.list)
            trueOrFalseBinding.rvStatement.layoutManager = LinearLayoutManager(activity)
            trueOrFalseBinding.rvStatement.adapter = statementAdapter

            trueOrFalseBinding.rvStatement.apply {
                adapter = statementAdapter
            }
            statementAdapter?.setOnTouchListener(this)
            question = it.question
            Firebase.analytics.logEvent("simm_sorting_started", null)
        }

        trueOrFalseBinding.cardTruthBox.setOnDragListener(statementDragListener)

        trueOrFalseBinding.cardFalseBox.setOnDragListener(statementDragListener)
        return  view
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private val statementDragListener = View.OnDragListener { view, dragEvent ->
        val dropArea = view as TextView

        var mp : MediaPlayer = MediaPlayer.create(context, R.raw.quiz_wrong_answer)

        when (dragEvent.action) {

            DragEvent.ACTION_DRAG_STARTED -> {

                draggingItem.alpha = 0.5f

                true


            }
            DragEvent.ACTION_DRAG_ENTERED -> {


                if (dropArea.text==trueOrFalseBinding.cardTruthBox.text){
                    trueOrFalseBinding.cardTruthBox.alpha = 0.3F

                }
                else{
                    trueOrFalseBinding.cardFalseBox.alpha = 0.3f

                }

                true


            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                //statementAdapter?.statementBinding?.tvStatement?.background?.alpha=1

                if (dropArea.text==trueOrFalseBinding.cardTruthBox.text){
                    trueOrFalseBinding.cardTruthBox.alpha = 0.5F

                }
                else {
                    trueOrFalseBinding.cardFalseBox.alpha = 0.5f

                }

                true
            }

            DragEvent.ACTION_DRAG_EXITED ->{
                //statementAdapter?.statementBinding?.tvStatement?.background?.alpha=1

                trueOrFalseBinding.cardTruthBox.alpha = 1.0f
                trueOrFalseBinding.cardFalseBox.alpha = 1.0f

                true
            }


            DragEvent.ACTION_DROP -> {
                //statementAdapter?.statementBinding?.tvStatement?.background?.alpha=1

                trueOrFalseBinding.cardTruthBox.alpha = 1.0f
                trueOrFalseBinding.cardFalseBox.alpha = 1.0f

                statementAdapter?.getItemList()?.remove(selectedItemModel)
                statementAdapter?.notifyDataSetChanged()

                if (dropArea.text == selectedItemModel.answer) {
                    if (mp.isPlaying) {
                        mp.pause() // Pause the current track
                    }
                    mp.release()
                    mp = MediaPlayer.create(context, R.raw.quiz_correct_answer)
                    mp.start()  // play sound
                    showPopUpDialog(R.layout.right_dialog)
                } else {
                    if (mp.isPlaying) {
                        mp.pause() // Pause the current track
                    }
                    mp.release()
                    mp = MediaPlayer.create(context, R.raw.quiz_wrong_answer)
                    mp.start()  // play sound
                    showPopUpDialog(R.layout.wrong_dialog)
                    errorCount++

                    if(errorCount==3){
                        sessionViewModel.decrementHeartsCount()
                        errorCount=0
                    }
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                trueOrFalseBinding.cardTruthBox.alpha = 1.0f
                trueOrFalseBinding.cardFalseBox.alpha = 1.0f
                draggingItem.alpha = 1f


                if(statementAdapter?.getItemList()?.size == 0){
                    if (!finished) {
                        finished = true
                        sessionViewModel.changeProgress()
                            showBottomSheet()

                    }
                }
                true
            }
            else -> {
                false
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(this.requireContext())
        val view = layoutInflater.inflate(R.layout.true_or_false_bottom_sheet, null)
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.window?.setDimAmount(0f)
        dialog.show()
        lifecycleScope.launch(Dispatchers.Main){
            delay(2000)
            dialog.window?.setDimAmount(.5f)

        }
        val bottomSheetLayout = view.findViewById<ConstraintLayout>(R.id.heart_parent_layout)

        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheetLayout.setBackgroundResource(R.drawable.round_corners_green)

        val btOk = view.findViewById<TextView>(R.id.btn_continue_quest)
        btOk.setOnClickListener {
            if (question != null) {
                Firebase.analytics.logEvent("simm_sorting_completed", null)
            }

            sessionViewModel.checkForNextQuestion(true)
            dialog.dismiss()

        }

        // haptics
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        // this type of vibration requires API 29
        val vibrationEffect: VibrationEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val singleClickPattern = longArrayOf(0, 10)
            val singleClickAmplitude = intArrayOf(0, 180)

            vibrationEffect = VibrationEffect.createWaveform(singleClickPattern, singleClickAmplitude, -1)

            // it is safe to cancel other vibrations currently taking place
            vib.cancel();
            vib.vibrate(vibrationEffect)
        }
    }


    private fun showPopUpDialog(resId: Int) {
        val view = View.inflate(context, resId, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            dialog.dismiss()
        }
    }

    private class MyDragShadowBuilder(v: View, tPoint: Point, statementBinding: StatementBinding) : View.DragShadowBuilder(v) {
        //private val shadow = ColorDrawable(Color.TRANSPARENT)

        val touchPoint = tPoint
        val stateBinding = statementBinding

        override fun onProvideShadowMetrics(size: Point?, touch: Point?) {



            var width: Int = view.width
            var height: Int = view.height
            if(width < 2) width = 784  // to prevent "Drag shadow dimensions must not be negative" error
            if(height < 2) height = 299  // to prevent "Drag shadow dimensions must not be negative" error


           // shadow.setBounds(0, 0, width, height)
            size?.set(width, height)

            var xpos = touchPoint.x
            var ypos = touchPoint.y - height/2
            if(xpos < 1) xpos = width/2  // to prevent "Drag shadow dimensions must not be negative" error
            if(ypos < 1) ypos = height/2  // to prevent "Drag shadow dimensions must not be negative" error

            touch?.set(xpos, ypos)
        }

        override fun onDrawShadow(canvas: Canvas?) {



//            val view = StatementBinding.bind()
//            view.text = "This is a custom drawn textview"
//            view.setBackgroundColor(Color.RED)
//            view.gravity = Gravity.CENTER



            super.onDrawShadow(canvas)
        }
    }

    private companion object

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onItemTouch(
        view: View,
        statement: Statement,
        motionEvent: MotionEvent,
        statementBinding: StatementBinding
    ) {
        statement.also { selectedItemModel = it }
        val item = ClipData.Item(dragMessage)
        val dragData = ClipData(
            dragMessage,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item
        )
        val viewLocation : IntArray = intArrayOf(0, 0)
        val fingerOffset = Point(0, 0)
        view.getLocationInWindow(viewLocation)

        fingerOffset.x = motionEvent.rawX.toInt() - viewLocation[0]
        fingerOffset.y = motionEvent.rawY.toInt() - viewLocation[1]

        //val noShadow = statementBinding.tvStatement.background.alpha=0
        val myShadow = MyDragShadowBuilder(view, fingerOffset,statementBinding)


       //view.startDrag(dragData,myShadow, view, DRAG_FLAG_OPAQUE)
        //val visib = statementBinding.tvStatement.background.setVisible(false,false)
        view.startDragAndDrop(dragData, myShadow, view, DRAG_FLAG_OPAQUE)
        draggingItem = statementBinding.tvStatement
        //draggingItem.alpha = 0.5f


    }


}