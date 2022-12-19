package com.intractable.simm.gamelogic

import com.intractable.simm.model.QuestionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ISessionLoader {
    fun getSessionJsonDesc(): String

    fun getMCQData(): Flow<ArrayList<QuestionItem>>
}