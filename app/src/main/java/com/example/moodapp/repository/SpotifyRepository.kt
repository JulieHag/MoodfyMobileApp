package com.example.moodapp.repository

import com.example.moodapp.data.api.RetrofitInstance

class SpotifyRepository {



    //querying spotify api to get currently playing track
    suspend fun getCurrentTrack(token: String, marketCode: String) =
        RetrofitInstance.api.getCurrentTrack(token, marketCode)

    //request spotify api to return a list of user's playlists
    suspend fun getUserPlaylists(token: String) =
        RetrofitInstance.api.getUserPlaylists(token)

    //post currently playing song to users mood playlist
    suspend fun addToMoodPlaylist(token: String, playlistId: String, trackUri: String)=
        RetrofitInstance.api.addToMoodPlaylist(token, playlistId, trackUri)

    // Gets current user's profile
    suspend fun getUserProfile(token: String) =
        RetrofitInstance.api.getUserProfile(token)

    // Creates a new playlist
    suspend fun createUserPlaylist(token: String, userId: String, name: String) =
        RetrofitInstance.api.createUserPlaylist(token, userId, name)
}