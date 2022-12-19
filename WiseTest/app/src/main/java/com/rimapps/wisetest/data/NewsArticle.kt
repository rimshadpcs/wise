package com.rimapps.wisetest.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "news_articles")
data class NewsArticle (
    val title: String?,
    @PrimaryKey val url : String,
    val thumbnailUrl: String?

) : Parcelable

//@Entity(tableName = "news_feed")
//data class NewsFeed(
//    val articleUrl: String,
//    @PrimaryKey(autoGenerate = true)val id:Int=0
//)