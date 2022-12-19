package com.rimapps.wisetest.features.newsFeed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rimapps.wisetest.data.NewsArticle
import com.rimapps.wisetest.data.NewsRepository
import com.rimapps.wisetest.util.Resource
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private  val repository: NewsRepository
): ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()
    private val refreshTriggerChannel = Channel<Unit>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val newsFeed =refreshTrigger.flatMapLatest {
        repository.getNewsFeed(
            onFetchFailed = {t->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(t))
                }

            }
        )

    } .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onStart() {
        if (newsFeed.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Unit)
            }
        }
    }


    fun onManualRefresh() {
        if (newsFeed.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Unit)
            }
        }
    }
    sealed class Event{
        data class  ShowErrorMessage(val error: Throwable): Event()
    }

}
