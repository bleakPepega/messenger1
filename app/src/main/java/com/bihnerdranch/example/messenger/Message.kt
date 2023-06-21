package com.bihnerdranch.example.messenger

data class Message(
    val text: String,
    val filePath: String?,
    val position: Position,
    val dataFromFile: ByteArray?
    ) {
    enum class Position { LEFT, RIGHT }
}