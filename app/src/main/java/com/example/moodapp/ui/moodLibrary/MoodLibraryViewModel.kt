package com.example.moodapp.ui.moodLibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moodapp.models.userPlaylists.UserPlaylistsResponse
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.utils.Resource
import com.example.moodapp.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response

class MoodLibraryViewModel(
    val spotifyRepository: SpotifyRepository,
    application: Application
) : AndroidViewModel(application) {

    val TAG = "MoodLibraryViewModel"

    private val _text = MutableLiveData<String>().apply {
        value = "No Moodfy playlists to show yet..."
    }
    val text: LiveData<String> = _text
    val userPlaylist: MutableLiveData<Resource<UserPlaylistsResponse>> = MutableLiveData()
    private var sessionManager: SessionManager = SessionManager(application.applicationContext)

    init {

        getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")
    }


    /**
     * coroutine stays alive only as long as view model stays alive
     */
    fun getUserPlaylists(token: String) = viewModelScope.launch {
        //show progress bar until results loaded
        userPlaylist.postValue(Resource.Loading())
        val playlistResponse = spotifyRepository.getUserPlaylists(token)
        //post the response success or error state to live data which fragment can observe
        userPlaylist.postValue(handlePlaylistResponse(playlistResponse))


    }


    /**
     * Function to handle the response from the api call and will return the succesful or error response
     */
    private fun handlePlaylistResponse(response: Response<UserPlaylistsResponse>) : Resource<UserPlaylistsResponse>{
        if(response.isSuccessful){
            //check body isn't null
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)

            }
        }
        return Resource.Error(response.message())
    }
}