package com.jhag.moodapp.data.models.currentlyPlaying

data class CurrentTrackResponse(
    val context: Context,
    val currently_playing_type: String,
    val is_playing: Boolean,
    val item: Item,
    val progress_ms: Int,
    val timestamp: Long
)