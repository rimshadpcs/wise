package com.intractable.simm.view.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.intractable.simm.R
import com.intractable.simm.adapters.StatementAdapter
import com.intractable.simm.databinding.FragmentTrueOrFalseBinding
import com.intractable.simm.model.Statement
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentTrueOrFalse : Fragment(),StatementAdapter.OnItemLongClickListener {

    private lateinit var trueOrFalseBinding: FragmentTrueOrFalseBinding
    private var statementAdapter: StatementAdapter? = null
    private val sessionViewModel: SessionViewModel by activityViewModels()

    private val dragMessage = "Added"
    private lateinit var draggingItem: View
    private lateinit var selectedItemModel: Statement
    private var finished = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        trueOrFalseBinding = FragmentTrueOrFalseBinding.inflate(inflater, container, false)
        val view = trueOrFalseBinding.root


        sessionViewModel.getToFList().observe(context as SessionActivity, Observer {
            if (!it.isEmpty()){
                Log.e("item",it[0].statementDesc+"hg")
                trueOrFalseBinding.tvStatementQuestion.text = sessionViewModel.ToFData.value?.question
                trueOrFalseBinding.cardLeftBox.text = sessionViewModel.ToFData.value?.button_left
                trueOrFalseBinding.cardRightBox.text = sessionViewModel.ToFData.value?.button_right
                statementAdapter = StatementAdapter(activity!!, it)
                trueOrFalseBinding.rvStatement.layoutManager = LinearLayoutManager(activity)
                trueOrFalseBinding.rvStatement.adapter = statementAdapter

                trueOrFalseBinding.rvStatement.apply {
                    adapter = statementAdapter
                    postponeEnterTransition()
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                }
                statementAdapter?.setOnItemLongClickListener(this)
            }
        })
        trueOrFalseBinding.cardLeftBox.setOnDragListener(statementDragListener)

        trueOrFalseBinding.cardRightBox.setOnDragListener(statementDragListener)
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private val statementDragListener = View.OnDragListener { view, dragEvent ->

        draggingItem

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {

                trueOrFalseBinding.cardLeftBox.alpha = 0.3f
                trueOrFalseBinding.cardRightBox.alpha = 0.3f

                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                trueOrFalseBinding.cardLeftBox.alpha = 0.5f
                trueOrFalseBinding.cardRightBox.alpha = 0.5f
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                trueOrFalseBinding.cardLeftBox.alpha = 1.0f
                trueOrFalseBinding.cardRightBox.alpha = 1.0f

//                draggableItem.visibility = View.VISIBLE
//                view.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {

                trueOrFalseBinding.cardLeftBox.alpha = 1.0f
                trueOrFalseBinding.cardRightBox.alpha = 1.0f

//                draggableItem.x = dragEvent.x - (draggableItem.width / 2)
//                draggableItem.y = dragEvent.y - (draggableItem.height / 2)

                val dropArea = view as TextView

                if (dropArea.text == selectedItemModel.answer) {
                    showPopUpDialog(R.layout.right_dialog)
                } else {
                    showPopUpDialog(R.layout.wrong_dialog)
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {

//                draggableItem.visibility = View.INVISIBLE
//                view.invalidate()

                //on drop event remove the card from parent viewGroup
//                val parent = draggableItem.parent as RecyclerView
//                parent.removeView(draggableItem)

                statementAdapter?.getItemList()?.remove(selectedItemModel)
                statementAdapter?.notifyDataSetChanged()

                if(statementAdapter?.getItemList()?.size == 0){
                    if (!finished) {
                        finished = true

                        Toast.makeText(context, "Finished!", Toast.LENGTH_SHORT).show()
                        sessionViewModel.checkForNextQuestion()
                    }
                }
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onItemLongClick(view: View, statement: Statement) {
        selectedItemModel = statement;
        val item = ClipData.Item(dragMessage)
        val dragData = ClipData(
            dragMessage,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item
        )
        val myShadow = MyDragShadowBuilder(view)
        view.startDragAndDrop(dragData, myShadow, null, 0)
        draggingItem = view
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

    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
        private val shadow = ColorDrawable(Color.LTGRAY)

        override fun onProvideShadowMetrics(size: Point?, touch: Point?) {

            val width: Int = view.width
            val height: Int = view.height
            shadow.setBounds(0, 0, width, height)
            size?.set(width, height)
            touch?.set(width, height)
        }

        override fun onDrawShadow(canvas: Canvas?) {
            super.onDrawShadow(canvas)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentTrueOrFalse().apply {
                arguments = Bundle().apply {
                }
            }
    }

}