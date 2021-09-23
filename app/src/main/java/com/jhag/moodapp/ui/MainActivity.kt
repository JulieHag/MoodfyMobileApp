package com.jhag.moodapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jhag.moodapp.R
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

    val positiveButtonClick = { _: DialogInterface, _: Int ->
        spotifyAccess()

    }

    val negativeButtonClick = { _: DialogInterface, _: Int ->
        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        sessionManager.clearPrefs()
        Log.d(TAG, "${sessionManager.fetchAuthToken()}")

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






    }



    /**
     * Asks user for permission to access their spotify
     */
    fun spotifyAccess() {

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
                    //save AUTH_TOKEN to session
                    sessionManager.saveAuthToken(response.accessToken)

                }


                AuthenticationResponse.Type.ERROR -> {
                    //Unsuccessful auth i.e user presses cancel
                    //Log.d(TAG, "Unsuccessful auth")
                    loginPermissionAlert()
                }
                // Most likely auth flow was cancelled i.e. back button pressed
                else -> {
                    // Handle other cases
                    //Log.d(TAG, "auth fow cancelled")
                    loginPermissionAlert()
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: called")
        if (sessionManager.fetchAuthToken() == null){
            spotifyAccess()
        }

    }


    fun loginPermissionAlert() {


        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Permission required")
            setMessage("This app requires access to your Spotify account, without this the app will not function correctly. Press 'OK' to allow access.")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Cancel", negativeButtonClick)
            show()
        }

    }


}


