package com.rimapps.NoteApp

import java.io.File
import java.security.AuthProvider
import java.security.PrivateKey

class MainRepository @Inject constructor(
    private val authenticator: Authenticator,
    private val authProvider: AuthProvider,
    private val fileLogger: FileLogger
        ){

    suspend fun loginUser(email: String, password:String){
        try {
            authProvider.login(email,password){
                catch(e: exception){
                    fileLogger.logError(e.message.toString())
                }
            }
    }
}