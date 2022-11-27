package com.rimapps.NoteApp

import android.hardware.biometrics.BiometricPrompt.AuthenticationResult

interface Authenticator {

    suspend fun signInWithEmailAndPassword(email: String, password: String)
}

class FirebaseAuthenticator: Authenticator{

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
    FirebaseAuth.getInsctance().signInWithEmailAndPassword(email,password)

    }
}