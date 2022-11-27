package com.rimapps.NoteApp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ResourseComparerTest {

    private lateinit var resourseComparer : ResourseComparer

    @Before
    fun setup(){
        resourseComparer = ResourseComparer()
    }
 @Test
 fun stringResourceSameAsGivenString_returnsTrue(){
     val context  = ApplicationProvider.getApplicationContext<Context>()
     val result = resourseComparer.isEqual(context, R.string.app_name, "Note app")
     assertThat(result).isTrue()
 }

@Test
fun stringResourcesDifferentAsGivenString_returnsFalse(){
    val context = ApplicationProvider.getApplicationContext<Context>()
    val result  = resourseComparer.isEqual(context,R.string.app_name, "hello")
    assertThat(result).isFalse()
}
}