package com.rimapps.wisetest.features.newsFeed

import androidx.recyclerview.widget.DiffUtil
import com.rimapps.wisetest.data.NewsArticle

class NewsFeedDiffUtil : DiffUtil.ItemCallback<NewsArticle>() {

    override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle) =
        oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle) =
        oldItem == newItem
}