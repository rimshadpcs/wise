package com.rimapps.wisetest.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

inline fun <ResultType,RequestType> networkBoundResource(
    crossinline query:() -> Flow<ResultType>,
    crossinline fetch: suspend ()->RequestType,
    crossinline saveFetchResult: suspend (RequestType)-> Unit,
    crossinline shouldFetch:(ResultType)-> Boolean = {true}   ,
    crossinline onFetchFailed: (Throwable)->Unit = {},
) = channelFlow {
    val data = query().first()

    if(shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }
        try {
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        }catch (t:Throwable){
            onFetchFailed(t)
            loading.cancel()
            query().collect{
                send(Resource.Error(t,it))}
            }
        }else{
            query().collect{send(Resource.Success(it))}
    }
}