package com.example.moodapp.repository

import com.example.moodapp.data.api.RetrofitInstance

class SpotifyRepository {



    //querying spotify api to get currently playing track
    suspend fun getCurrentTrack(token: String, marketCode: String) =
        RetrofitInstance.api.getCurrentTrack(token, marketCode)

    //request spotify api to return a list of user's playlists
    suspend fun getUserPlaylists(token: String) =
        RetrofitInstance.api.getUserPlaylists(token)
}