package com.example.simmone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simmone.R
import com.example.simmone.databinding.StatementBinding
import com.example.simmone.model.Statement

class StatementAdapter(
    private val context: Context,
    private val statementList: ArrayList<Statement>
) :
    RecyclerView.Adapter<StatementAdapter.StatementViewHolder>() {

    private var mListener: OnItemLongClickListener? = null

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, statement: Statement)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val statementBinding: StatementBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.statement_list, parent, false)
        return StatementViewHolder(statementBinding, mListener)

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
        private val listener: OnItemLongClickListener?

    ) : RecyclerView.ViewHolder(statementBinding.root) {

        fun bind(statementViewModel: Statement) {
            this.statementBinding.statementModel = statementViewModel

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(itemView, statementViewModel)
                true
            }

            statementBinding.executePendingBindings()
        }
    }

}