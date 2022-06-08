package com.example.simmone.di

import android.content.Context
import com.example.simmone.R
import com.example.simmone.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    /*Hilt test resources*/
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication{
        return app as BaseApplication
    }

    /* provideRandomString is a dependency we can inject to something else.
       This string object is gonna exist as long as the Application is alive */

    @Singleton
    @Provides
    fun provideRandomString(): String{
        return "Hey look a random string"
    }
    /*Hilt test resources ENDS ^*/

    /*Bubble sample string resources*/
    @Singleton
    @Provides
    @Named("bubbleSampleMessage")
    fun provideBubbleSampleMessage(
        @ApplicationContext context: Context) = context.getString(R.string.sample)

    @Singleton
    @Provides
    @Named("bubbleSampleButton")
    fun provideBubbleSampleButton(
        @ApplicationContext context: Context) = context.getString(R.string.continu)
}
