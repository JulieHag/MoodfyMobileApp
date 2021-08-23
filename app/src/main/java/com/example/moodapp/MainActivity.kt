package com.example.moodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moodapp.databinding.ActivityMainBinding
import com.example.moodapp.other.Constants.Companion.CLIENT_ID
import com.example.moodapp.other.Constants.Companion.REDIRECT_URI
import com.example.moodapp.other.Constants.Companion.REQUEST_CODE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse



class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_mood_library,
                R.id.navigation_mood_boost,
                R.id.navigation_mood_music
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
        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)

        /**
        val request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN)
        AuthenticationClient.openLoginActivity(
            this,
            Constants.REQUEST_CODE,
            request
        ) **/


    }



    /**
    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest{
        return AuthenticationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
            .setShowDialog(false)
            .setScopes(arrayOf("streaming"))
            .build()

    } **/


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


}


