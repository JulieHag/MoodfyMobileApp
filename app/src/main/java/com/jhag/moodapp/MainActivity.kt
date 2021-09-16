package com.jhag.moodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jhag.moodapp.databinding.ActivityMainBinding
import com.jhag.moodapp.utils.Constants.Companion.CLIENT_ID
import com.jhag.moodapp.utils.Constants.Companion.REDIRECT_URI
import com.jhag.moodapp.utils.Constants.Companion.REQUEST_CODE
import com.jhag.moodapp.utils.SessionManager
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_mood_music,
                R.id.navigation_mood_library
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // code adapted from spotify authentication guide
        val builder = AuthenticationRequest.Builder(
            CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        builder.setScopes(
            arrayOf(
                "user-read-currently-playing",
                "user-read-playback-state",
                "playlist-modify-public",
                "playlist-modify-private",
                "ugc-image-upload"
            )
        )
        //to give user chance to log out
        builder.setShowDialog(true)
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)



    }


    /**
     * code adapted from spotify authentication guide
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    //handle successful response - get requests from API
                    Log.d(TAG, "Successful auth")
                    //save AUTH_TOKEN to session
                    sessionManager.saveAuthToken(response.accessToken)

                }


                AuthenticationResponse.Type.ERROR -> {
                    //handle error response - alert dialog
                    Log.d(TAG, "Unsuccessful auth")
                }
                // Most likely auth flow was cancelled
                else -> {
                    // Handle other cases
                    Log.d(TAG, "auth fow cancelled")
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()


    }


}


