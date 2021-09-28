package com.jhag.moodapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jhag.moodapp.repository.SpotifyRepository

/**
 * Needed so that the MoodLibraryViewModel can have a constructor
 */

class MoodLibraryViewModelProviderFactory(
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