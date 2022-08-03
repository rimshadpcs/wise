package com.intractable.simm.view.activities

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intractable.simm.R
import com.intractable.simm.adapters.StatementAdapter
import com.intractable.simm.databinding.ActivityTrueOrFalseBinding
import com.intractable.simm.model.Statement
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.StatementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrueOrFalseActivity : AppCompatActivity(),
    StatementAdapter.OnItemLongClickListener {
    private lateinit var trueOrFalseBinding: ActivityTrueOrFalseBinding
    private var rvStatement: RecyclerView? = null
    private var statementAdapter: StatementAdapter? = null
    lateinit var appUtil: AppUtil

    private val dragMessage = "Added"
    private lateinit var draggingItem: View
    private lateinit var selectedItemModel: Statement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trueOrFalseBinding = ActivityTrueOrFalseBinding.inflate(layoutInflater)
        setContentView(trueOrFalseBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        rvStatement = findViewById(R.id.rvStatement)
        val statementViewModel = ViewModelProvider(this)[StatementViewModel::class.java]
        statementViewModel.generateStatement()

        statementViewModel.newMStatementList.observe(this) {
            statementAdapter = StatementAdapter(this@TrueOrFalseActivity, it)
            rvStatement!!.layoutManager = LinearLayoutManager(this@TrueOrFalseActivity)
            rvStatement!!.adapter = statementAdapter
            statementAdapter?.setOnItemLongClickListener(this)
        }

        trueOrFalseBinding.cardTruthBox.setOnDragListener(statementDragListener)

        trueOrFalseBinding.cardFalseBox.setOnDragListener(statementDragListener)

    }

    private val statementDragListener = View.OnDragListener { view, dragEvent ->

        draggingItem
        val dropArea = view as TextView

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
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

                if (dropArea.text==trueOrFalseBinding.cardTruthBox.text){
                    trueOrFalseBinding.cardTruthBox.alpha = 0.5F

                }
                else{
                    trueOrFalseBinding.cardFalseBox.alpha = 0.5f

                }
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                trueOrFalseBinding.cardTruthBox.alpha = 1.0f
                trueOrFalseBinding.cardFalseBox.alpha = 1.0f

                true
            }

            DragEvent.ACTION_DROP -> {


                trueOrFalseBinding.cardTruthBox.alpha = 1.0f
                trueOrFalseBinding.cardFalseBox.alpha = 1.0f

                statementAdapter?.getItemList()?.remove(selectedItemModel)
                statementAdapter?.notifyDataSetChanged()


                if (dropArea.text == selectedItemModel.answer) {
                    showPopUpDialog(R.layout.right_dialog)
                } else {
                    showPopUpDialog(R.layout.wrong_dialog)
                }
                true

            }
            DragEvent.ACTION_DRAG_ENDED -> {

                if(statementAdapter?.getItemList()?.size == 0){
                    Toast.makeText(this@TrueOrFalseActivity, "Finished!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SessionActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onItemLongClick(view: View, statement: Statement) {
        selectedItemModel = statement
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
        val view = View.inflate(this, resId, null)
        val builder = AlertDialog.Builder(this@TrueOrFalseActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            dialog.dismiss()
        }
    }
}

private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
    private val shadow = ColorDrawable(Color.LTGRAY)

    override fun onProvideShadowMetrics(size: Point?, touch: Point?) {

        val width: Int = view.width
        val height: Int = view.height
        shadow.setBounds(0, 0, width, height)
        size?.set(width, height)
        touch?.set(width/2, height/2)
    }

    override fun onDrawShadow(canvas: Canvas?) {
        super.onDrawShadow(canvas)
    }
}

