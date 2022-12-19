package com.intractable.simm.utils

object UnityConstants {

    enum class TraceActivity {
        ActivityStart, HintOn, HintOff, CheckpointOne, CheckpointTwo, CheckpointThree, AllCheckpoints, ActivityComplete, ActivityFailed, CircleFailed, FlowerFailed, ShowingIndicator, SectionCompleted, SectionFailed
    }

    enum class PinchZoomActivity {
        ActivityStart, ShapeBelowSize, ShapeAboveSize, HintOn, HintOff, TooFast, TooSlow, ActivityComplete, SectionCompleted
    }

    enum class GestureNavigation {
        ActivityStart, SwipingUp, SwipingLeft, TooFarLeft, TooFarRight, ScreenClick, ActivityComplete, SectionCompleted
    }

    enum class GesturePrevious {
        ActivityStart, ClickNext, ClickBack, ClickNextAgain, SwipeBack, ActivityComplete, SectionCompleted
    }

    enum class GestureHome {
        ActivityStart, SwipingUp, ActivityComplete, SectionCompleted
    }

    enum class GestureHomeNewsScreen {
        ActivityStart, SwipingLeft, SwipingRight, SwipingDown, SwipingUp, ReturnToHome, ActivityComplete, SectionCompleted
    }

    enum class GestureAppList {
        ActivityStart, OpenAppList, SwipingDown, ReturnToTop, ReturnToHome, ActivityComplete, SectionCompleted
    }

    enum class GestureQuickSettings {
        ActivityStart, OpenQSNotifications, CloseQSNotifications, OpenQSNotificationsAgain, ActivityComplete, SectionCompleted
    }

    enum class Comic {
        ComicStart, PreviousPages, FinalPage
    }

    enum class TextGame {
        TextStoryStart, ResultsPage, ActivityComplete, GamePassed, GameFailed
    }
}