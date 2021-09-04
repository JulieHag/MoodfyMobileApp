package com.example.moodapp.models.createPlaylist

data class Tracks(
    val href: String,
    val items: List<Any>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)