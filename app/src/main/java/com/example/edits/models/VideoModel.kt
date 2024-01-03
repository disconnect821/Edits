package com.example.edits.models

import com.google.firebase.Timestamp

data class VideoModel(
    val videoId : String = "",
    val title : String = "",
    val url : String ="",
    val uploaderId : String ="",
    val createdTime : Timestamp = Timestamp.now()
)
