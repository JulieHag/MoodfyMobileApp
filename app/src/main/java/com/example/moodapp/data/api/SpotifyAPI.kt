package com.example.moodapp.data.api

import com.example.moodapp.models.currentlyPlaying.CurrentTrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 *  Interface used to define requests
 */
interface SpotifyAPI {

    @GET("v1/me/player/currently-playing")
    //want to execute function asynchronously using coroutines
    suspend fun getCurrentTrack(
        @Query("market")
        marketCode: String = "GB",
        @Header("Authorization")
        token: String,

        ): Response<CurrentTrackResponse>
}