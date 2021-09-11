package com.example.moodapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.ui.moodLibrary.MoodLibraryViewModel

class SpotifyViewModelProviderFactory(
    val spotifyRepository: SpotifyRepository,
    private val application: Application
): ViewModelProvider.Factory {
   // sessionManager = SessionManager(applicationContext)

    /**
     * return new instance of view model and pass repository
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoodLibraryViewModel(spotifyRepository, application) as T
    }
}