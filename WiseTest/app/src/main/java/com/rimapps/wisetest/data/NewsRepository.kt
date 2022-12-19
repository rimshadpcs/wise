package com.rimapps.wisetest.data

import androidx.room.withTransaction
import com.rimapps.wisetest.api.NewsApi
import com.rimapps.wisetest.util.Resource
import com.rimapps.wisetest.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val newsArticleDatabase: NewsArticleDatabase
) {
    private val newsArticleDao = newsArticleDatabase.newsArticleDao()


     fun getNewsFeed(
         onFetchFailed:(Throwable)->Unit
     ): Flow<Resource<List<NewsArticle>>>  =
         networkBoundResource(
             query = {
                 newsArticleDao.getAllNewsArticles()
             }, fetch = {
                 val response = newsApi.getNewsFeed()
                 response.data
             },
             saveFetchResult = { it ->
                 val newsFeedArticles = it.map {
                     NewsArticle(
                        title = it.title,
                        url = it.url,
                        thumbnailUrl = it.image

                     )
                 }

                 newsArticleDatabase.withTransaction {
                     newsArticleDao.deleteAllNewsArticles()
                     newsArticleDao.insertArticles(newsFeedArticles)
                 }
             },
             onFetchFailed = { t->
                 if(t !is HttpException && t !is IOException){
                     throw t
                 }
                 onFetchFailed(t)
             }
         )

}