package com.rimapps.wisetest.features.newsFeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rimapps.wisetest.R
import com.rimapps.wisetest.data.NewsArticle
import com.rimapps.wisetest.databinding.ItemNewsBinding


class NewsFeedListAdapter (private val listener: OnItemClickListener):
    ListAdapter<NewsArticle,NewsFeedListAdapter.NewsFeedViewHolder>(NewsFeedDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewsFeedViewHolder(binding)
    }

    interface OnItemClickListener{
        fun onItemClick(newsArticle: NewsArticle)
    }
    override fun onBindViewHolder(
        holder:NewsFeedViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)
        if(currentItem != null){
            holder.bind(currentItem)
        }


    }
    inner class NewsFeedViewHolder(
        private val binding: ItemNewsBinding
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition

                if(position!=RecyclerView.NO_POSITION){
                    val item = getItem(position)

                    if(item!=null){
                        listener.onItemClick(item)
                    }
                }
            }
        }
        fun bind(article: NewsArticle){
            binding.apply {
                Glide.with(itemView)
                    .load(article.thumbnailUrl)
                    .error(R.drawable.placeholder)
                    .into(ivNewsTitle)
                tvTitle.text = article.title ?: ""
            }
        }

    }
}

