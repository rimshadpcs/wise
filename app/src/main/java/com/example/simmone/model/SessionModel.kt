package com.example.simmone.model

import java.io.Serializable

class SessionModel:Serializable {
    var sessionId = ""
    var activityList:ArrayList<String>? = null
}