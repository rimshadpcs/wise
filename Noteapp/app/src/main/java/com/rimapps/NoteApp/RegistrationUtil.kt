package com.rimapps.NoteApp

private val existingUsers = listOf("Peter", "Carl")

/**
 * input is not valid if
 * ...the username/passworrd is empty
 * ...the username is already taken
 * ...the confirmed password is not equal to real
 * ...the password contains less than 2 digits
 */



object RegistrationUtil {
    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmPassword:String
    ):Boolean{
        if(username.isEmpty() || password.isEmpty()){
            return false
        }
        if (username in existingUsers){
            return false
        }
        if (password!=confirmPassword){
            return false
        }
        if(password.count{it.isDigit()}<2){
            return false
        }
        return true
    }

}