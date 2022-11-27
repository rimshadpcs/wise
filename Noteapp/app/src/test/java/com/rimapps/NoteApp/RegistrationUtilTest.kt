package com.rimapps.NoteApp

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest{
    @Test
    fun `empty username return false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password returns true`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "Rimshad",
            "123",
            "123"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `username exists returns true`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "Carl",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `confirm password does not match return false`(){
        val result = RegistrationUtil.validateRegistrationInput(

             "Mohamed",
             "123",
            "124"
        )
        assertThat(result).isFalse()
    }

    fun `password does not have 2 digits return false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "John",
            "1aqa",
        "1aqa"
        )
        assertThat(result).isFalse()
    }
}