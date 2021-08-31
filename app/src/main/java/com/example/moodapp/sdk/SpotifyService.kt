package com.example.moodapp.sdk

import android.content.Context
import android.util.Log
import com.example.moodapp.utils.Constants.Companion.CLIENT_ID
import com.example.moodapp.utils.Constants.Companion.REDIRECT_URI
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

object SpotifyService {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val TAG = "SpotifyService"


    private var connectionParams: ConnectionParams =
        ConnectionParams.Builder(CLIENT_ID).setRedirectUri(
            REDIRECT_URI
        ).showAuthView(false).build()


    fun connect(context: Context, handler: (connected: Boolean) -> Unit) {
        if (spotifyAppRemote?.isConnected == true) {
            handler(true)
            return
        }
        val connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                this@SpotifyService.spotifyAppRemote = spotifyAppRemote
                handler(true)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, throwable.message, throwable)
                handler(false)
            }
        }
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }


    /**
    SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
    override fun onConnected(appRemote: SpotifyAppRemote) {
    spotifyAppRemote = appRemote
    Log.d("MainActivity", "Connected! Yay!")
    // Now you can start interacting with App Remote
    connected()
    }

    override fun onFailure(throwable: Throwable) {
    Log.e("MainActivity", throwable.message, throwable)
    // Something went wrong when attempting to connect! Handle errors here
    }
    })**/

    fun playingState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            val track: Track = it.track
            Log.d("SpotifyService", track.name + " by " + track.artist.name)
        }

    }


}