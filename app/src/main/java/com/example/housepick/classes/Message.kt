package com.example.housepick.classes

data class Message(
    val text: String,
    val isUser: Boolean // `true` if the message is from the user, `false` if from the AI
)
