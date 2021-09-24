package com.jhag.moodapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jhag.moodapp.MoodfyApplication
import com.jhag.moodapp.data.models.userPlaylists.UserPlaylistsResponse
import com.jhag.moodapp.repository.SpotifyRepository
import com.jhag.moodapp.utils.Resource
import com.jhag.moodapp.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Require context for this viewmodel in order to access the session manager which is why it inherits
 * from AndroidViewModel. Then we are able to access the application context which will stay alive as long as the application
 * is alive, so it is safe to use.
 */
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
    //getApplication<MoodfyApplication>() so that android knows which application class we are referring to
    private var sessionManager: SessionManager = SessionManager(getApplication<MoodfyApplication>().applicationContext)

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
     * Function to handle the response from the api call and will return the successful or error response
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