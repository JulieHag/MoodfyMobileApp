package com.example.moodapp.data.api

import com.example.moodapp.models.currentlyPlaying.CurrentTrackResponse
import com.example.moodapp.models.userPlaylists.UserPlaylistsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 *  Interface used to define requests
 */
interface SpotifyAPI {

    // Get request to see what the user is currently playing
    @GET("v1/me/player/currently-playing")
    //want to execute function asynchronously using coroutines
    suspend fun getCurrentTrack(
        @Header("Authorization")
        token: String,
        @Query("market")
        marketCode: String = "GB",


        ): Response<CurrentTrackResponse>

    // Get request which returns the current user's playlists
    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization")
        token: String
    ) : Response<UserPlaylistsResponse>
}