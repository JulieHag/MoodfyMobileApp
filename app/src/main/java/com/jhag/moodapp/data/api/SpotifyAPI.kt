package com.jhag.moodapp.data.api

import com.jhag.moodapp.data.models.addSongToPlaylist.AddSongToPlaylistResponse
import com.jhag.moodapp.data.models.createPlaylist.CreatePlaylistResponse
import com.jhag.moodapp.data.models.createPlaylistBody.CreatePlaylistBody
import com.jhag.moodapp.data.models.currentlyPlaying.CurrentTrackResponse
import com.jhag.moodapp.data.models.userPlaylists.UserPlaylistsResponse
import com.jhag.moodapp.data.models.userProfile.UserProfileResponse
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
    ): Response<UserPlaylistsResponse>

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

    // Gets current user's profile details
    @GET("v1/me")
    suspend fun getUserProfile(
        @Header("Authorization")
        token: String
    ): Response<UserProfileResponse>

    // Create a new playlist
    @POST("v1/users/{user_id}/playlists")
    suspend fun createUserPlaylist(
        @Header("Authorization")
        token: String,
        @Path("user_id")
        userId: String,
        @Body createPlaylistBody: CreatePlaylistBody
    ): Response<CreatePlaylistResponse>



}