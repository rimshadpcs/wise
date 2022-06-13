package com.example.simmone.di
import android.content.Context
import com.example.simmone.R
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

    /*Bubble sample string resources STARTS here*/
    @Singleton
    @Provides
    @Named("bubbleSampleMessage")
    fun provideBubbleSampleMessage(
        @ApplicationContext context: Context): String = context.getString(R.string.sample)

    @Singleton
    @Provides
    @Named("bubbleSampleButton")
    fun provideBubbleSampleButton(
        @ApplicationContext context: Context): String = context.getString(R.string.continu)
    /*Bubble sample string resources ENDS here*/

    /* MainActivity string resources STARTS here*/
    @Singleton
    @Provides
    @Named("continueButton")
    fun provideContinueButton(
        @ApplicationContext context: Context): String = context.getString(R.string.continu)
    /* MainActivity string resources ENDS here*/

    /* OperationResultActivity string resources STARTS here*/
    @Singleton
    @Provides
    @Named("outputMessageForNotification")
    fun provideOutputMessageForNotification(
        @ApplicationContext context: Context): String = context.getString(R.string.notificationResult)

    @Singleton
    @Provides
    @Named("nextButton")
    fun provideNextButtonString(
        @ApplicationContext context: Context): String = context.getString(R.string.next)
    /* OperationResultActivity string resources ENDS here*/


}
