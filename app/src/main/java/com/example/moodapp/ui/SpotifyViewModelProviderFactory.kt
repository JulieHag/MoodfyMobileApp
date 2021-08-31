package com.example.moodapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodapp.repository.SpotifyRepository

class SpotifyViewModelProviderFactory(
    val spotifyRepository: SpotifyRepository
): ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SpotifyViewModel(spotifyRepository) as T
    }
}