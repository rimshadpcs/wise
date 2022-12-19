package com.rimapps.wisetest.features.newsFeed

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.rimapps.wisetest.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.rimapps.wisetest.R
import com.rimapps.wisetest.data.NewsArticle
import org.mockito.Mockito.*

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NewsFeedFragmentTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun clickRecyclerView_navigateToDetailFragment(newsArticle: NewsArticle){
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<NewsFeedFragment> {

            Navigation.setViewNavController(requireView(),navController)
        }
        onView(withId(R.id.rv_news_feed)).perform(click())

        verify(navController).navigate(
            NewsFeedFragmentDirections.actionFeedFragmentToDetailsFragment(newsArticle)
        )
    }
}
