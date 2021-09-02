package com.example.moodapp.models.userPlaylists

data class UserPlaylistsResponse(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)