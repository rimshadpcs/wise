package com.rimapps.wisetest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    @Query("SELECT * FROM news_articles")
    fun getAllNewsArticles(): Flow<List<NewsArticle>>

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insertArticles(article: List<NewsArticle>)

//    @Insert(onConflict= OnConflictStrategy.REPLACE)
//    suspend fun insertNewsFeed(breakingNews: List<NewsFeed>)

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllNewsArticles()
}