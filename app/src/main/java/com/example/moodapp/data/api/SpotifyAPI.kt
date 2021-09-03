package com.example.moodapp.data.api

import com.example.moodapp.models.addSongToPlaylist.AddSongToPlaylistResponse
import com.example.moodapp.models.currentlyPlaying.CurrentTrackResponse
import com.example.moodapp.models.userPlaylists.UserPlaylistsResponse
import retrofit2.Response
import retrofit2.http.*

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

    // post currently playing song to user's mood playlist
    @POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addToMoodPlaylist(
        @Header("Authorization")
        token: String,
        @Path("playlist_id")
        playlistId: String,
        @Query("uris")
        trackUri: String
    ): Response<AddSongToPlaylistResponse>
}