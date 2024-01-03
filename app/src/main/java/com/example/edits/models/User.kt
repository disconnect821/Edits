package com.example.edits.models

data class User(
    val id : String= "",
    val email : String ="",
    val username : String = "",
    val profilePic : String = "",
    val followList : MutableList<String> = mutableListOf(),
    val followingList : MutableList<String> = mutableListOf()
)
