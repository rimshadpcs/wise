package com.intractable.simm.model

import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import java.io.Serializable

class SessionModel:Serializable {
    var sessionId = ""
    var lessonNumber = ""
    var activityList:ArrayList<String>? = null
    var title: String = ""
    var comment: String = "Scroll the list below and start exploring the lessons!"
    var isActive: Boolean = true
    //
    var hasCheckMark: Boolean = false
    var hasPlayIcon: Boolean = false
    var hasLockIcon: Boolean = false
    var hasComingSoonState: Boolean = false
    var listIcon: Int = R.drawable.heart_icon
    var titleAnimation: Int = R.raw.phone_title
    var description: String = "short description"
    var longDescription: String = "long description"
    var expandable: Boolean = false
    var endAnim: Int = R.raw.simm_thumbsup
    var splashScreenImage: Int = R.drawable.simm_encouraging
    var splashScreenComment : String = "Keep learning!"
    var hasWidgetReveal: Boolean = false
    var isComic: Boolean = false
    var hasBackgroundImg: Boolean = false
    var hasLowerBackgroundImg: Boolean = false
    var backgroundImg: Int = R.drawable.homescreen_gracestory_top
    var lowerBackgroundImg: Int = R.drawable.homescreen_gracestory_bottom
    var hasLargeBackgroundImg: Boolean = false
    var largeBackgroundImg: Int = R.drawable.gracestory_1
    var isOnboarding = false
    var lockPageCharacter: String = ""
    var completionStatus: StorageManager.SessionStatus = StorageManager.SessionStatus.NotDone
}