package com.jhag.moodapp.ui

import androidx.lifecycle.ViewModel
import com.jhag.moodapp.repository.SpotifyRepository

class SpotifyViewModel(
    val spotifyRepository: SpotifyRepository
): ViewModel() {
}