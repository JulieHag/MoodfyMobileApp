package com.jhag.moodapp.data.models.userPlaylists

data class Item(
    val collaborative: Boolean,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Any>,
    val name: String,
    val owner: Owner,
    val `public`: Boolean,
    val snapshot_id: String,
    val tracks: Tracks,
    val type: String,
    val uri: String
)