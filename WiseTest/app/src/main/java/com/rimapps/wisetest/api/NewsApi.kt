package com.rimapps.wisetest.api

import com.rimapps.wisetest.BuildConfig
import retrofit2.http.GET

interface NewsApi {

    companion object{
        const val BASE_URL = "http://api.mediastack.com/v1/"
        const val API_KEY = BuildConfig.NEWS_API_ACCESS_KEY
    }

    @GET("news?&access_key=$API_KEY&countries=gb&limit=100")
    suspend fun  getNewsFeed(): NewsResponse

}