package com.rimapps.wisetest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsArticle::class], version = 1)
abstract class NewsArticleDatabase : RoomDatabase() {


    abstract fun newsArticleDao(): NewsArticleDao
}