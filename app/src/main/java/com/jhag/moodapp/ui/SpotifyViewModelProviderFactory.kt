package com.jhag.moodapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jhag.moodapp.repository.SpotifyRepository
import com.jhag.moodapp.ui.moodLibrary.MoodLibraryViewModel

class SpotifyViewModelProviderFactory(
    val spotifyRepository: SpotifyRepository,
    private val application: Application
): ViewModelProvider.Factory {

    /**
     * return new instance of view model and pass repository
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoodLibraryViewModel(spotifyRepository, application) as T
    }
}