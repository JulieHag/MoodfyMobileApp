package com.jhag.moodapp.models.currentlyPlaying

data class Album(
    val album_type: String,
    val external_urls: ExternalUrlsX,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val type: String,
    val uri: String
)