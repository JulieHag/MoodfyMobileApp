package com.example.moodapp.repository

import com.example.moodapp.data.api.RetrofitInstance
import com.example.moodapp.utils.SessionManager

class SpotifyRepository {

    private lateinit var sessionManager: SessionManager

    //querying spotify api
    suspend fun getCurrentTrack(token: String, marketCode: String) =
        RetrofitInstance.api.getCurrentTrack(token = "Bearer ${sessionManager.fetchAuthToken()}", marketCode = "UK")
}