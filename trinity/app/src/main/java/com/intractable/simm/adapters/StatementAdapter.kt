package com.intractable.simm.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intractable.simm.R
import com.intractable.simm.databinding.StatementBinding
import com.intractable.simm.model.Statement

class StatementAdapter(
    private val context: Context,
    private val statementList: ArrayList<Statement>
) :
    RecyclerView.Adapter<StatementAdapter.StatementViewHolder>() {

    public var mListener: OnItemTouchListener? = null
    public var statementBinding:StatementBinding? = null

    interface OnItemTouchListener {
        fun onItemTouch(
            view: View,
            statement: Statement,
            motionEvent: MotionEvent,
            statementBinding: StatementBinding,
        )
    }

    fun setOnTouchListener(listener: OnItemTouchListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        statementBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.statement_list, parent, false)
        return StatementViewHolder(statementBinding!!, mListener)

    }


    override fun getItemCount(): Int {
        return statementList.size
    }

    fun getItemList() : ArrayList<Statement>{
        return statementList;
    }

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        val statementViewModel = statementList[position]
        holder.bind(statementViewModel)

    }

    class StatementViewHolder(
        private val statementBinding: StatementBinding,
        private val listener: OnItemTouchListener?,

    ) : RecyclerView.ViewHolder(statementBinding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(statementViewModel: Statement) {
            this.statementBinding.statementModel = statementViewModel


            itemView.setOnTouchListener { view, motionEvent ->
                listener!!.onItemTouch(view,statementViewModel, motionEvent, statementBinding)
                true
            }

            statementBinding.executePendingBindings()
        }
    }

}