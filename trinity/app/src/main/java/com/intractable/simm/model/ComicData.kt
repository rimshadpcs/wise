package com.intractable.simm.model

data class ComicData(val title: String, val sceneOption: Int, val sessionOption: Int, val comicDetails: MutableList<ComicDetails>)

data class ComicDetails(var background: String, val comicSpeechBubbles: MutableList<ComicSpeechBubble>)

data class ComicSpeechBubble(
    var displayText: String,
    var fontSize: String,
    var textPosition: String,
    var textBoxSize: String,
    var textAlignment: String,
    var bubbleImage: String,
    var bubblePosition: String,
    var bubbleSize: String,
    var pageNumber: String
)