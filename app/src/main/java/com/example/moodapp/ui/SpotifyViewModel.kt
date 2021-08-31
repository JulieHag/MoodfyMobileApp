package com.example.moodapp.ui

import androidx.lifecycle.ViewModel
import com.example.moodapp.repository.SpotifyRepository

class SpotifyViewModel(
    val spotifyRepository: SpotifyRepository
): ViewModel() {
}