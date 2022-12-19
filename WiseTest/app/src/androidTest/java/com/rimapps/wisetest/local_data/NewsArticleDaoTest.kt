package com.rimapps.wisetest.local_data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rimapps.wisetest.data.NewsArticle
import com.rimapps.wisetest.data.NewsArticleDao
import com.rimapps.wisetest.data.NewsArticleDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//@SmallTest
//@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class NewsArticleDaoTest{

//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //@Inject
    //@Named("test_db")
    //lateinit var database: NewsArticleDatabase

    private lateinit var database: NewsArticleDatabase
    private lateinit var dao: NewsArticleDao


    @Before
    fun setup(){
//        hiltRule.inject()
//        dao = database.newsArticleDao()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsArticleDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.newsArticleDao()
    }

    @After
    fun teardown(){
        database.close()
    }
    @Test
    fun insertNewsArticle() = runTest {
        val testArticle = NewsArticle("Time traveller shares footage from three weeks in the future showing who wins the World Cup","https://www.ladbible.com/sport/time-traveller-who-wins-world-cup-2022-20221128","https://images.ladbible.com/resize?type=webp&quality=70&width=671&fit=contain&gravity=null&dpr=2&url=https://eu-images.contentstack.com/v3/assets/bltcd74acc1d0a99f3a/bltb6064933a4b82b9c/6384c84adb8e364b186bfb6c/Most_prolific_speed_camera_in_the_UK_has_caught_almost_50_000_drivers_this_year_(42).png")
        val testItem = listOf(testArticle)
        dao.insertArticles(testItem)
//        val testFeed = NewsFeed(testArticle.url)
//        val feedTestItem = listOf(testFeed)
//        dao.insertNewsFeed(feedTestItem)

        val allNewsArticles = dao.getAllNewsArticles().first()

        assertThat(allNewsArticles).contains(testArticle)
    }

    @Test
    fun deleteAllArticles()= runTest {

        val testArticle = NewsArticle("Time traveller claims discovery of mysterious sea creature will change world","https://www.dailystar.co.uk/news/weird-news/time-traveller-claims-discovery-mysterious-28766022","https://i2-prod.dailystar.co.uk/incoming/article28766081.ece/ALTERNATES/s615b/1_A-SELF-proclaimed-time-traveller-from-2198-claims-experts-will-soon-make-a-chilling-ocean-discovery.jpg")
        val testItem = listOf(testArticle)
        dao.insertArticles(testItem)
//        val testFeed = NewsFeed(testArticle.url)
//        val feedTestItem = listOf(testFeed)
//        dao.insertNewsFeed(feedTestItem)

        dao.deleteAllNewsArticles()
        val allArticles = dao.getAllNewsArticles().first()

        assertThat(allArticles).doesNotContain(testArticle)


    }

}
