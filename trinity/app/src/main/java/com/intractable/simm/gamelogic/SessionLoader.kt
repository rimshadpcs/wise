package com.intractable.simm.gamelogic

import com.intractable.simm.model.QuestionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SessionLoader: ISessionLoader {

    override fun getSessionJsonDesc(): String {
        return "{ sessionId: \"notifications\" }"
    }

    override fun getMCQData(): Flow<ArrayList<QuestionItem>>  {
        return flow {
            emit(ArrayList<QuestionItem>())
        }
    }

    companion object {
        val instance = SessionLoader()
    }

}