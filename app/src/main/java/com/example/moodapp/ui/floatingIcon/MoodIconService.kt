package com.example.moodapp.ui.floatingIcon

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.moodapp.R
import com.example.moodapp.models.createPlaylistBody.CreatePlaylistBody
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.utils.Constants.Companion.AMUSED_MF
import com.example.moodapp.utils.Constants.Companion.ANGRY_MF
import com.example.moodapp.utils.Constants.Companion.CALM_MF
import com.example.moodapp.utils.Constants.Companion.EXCITED_MF
import com.example.moodapp.utils.Constants.Companion.HAPPY_IMAGE
import com.example.moodapp.utils.Constants.Companion.HAPPY_MF
import com.example.moodapp.utils.Constants.Companion.LOVE_MF
import com.example.moodapp.utils.Constants.Companion.NOSTALGIC_MF
import com.example.moodapp.utils.Constants.Companion.PRIDE_MF
import com.example.moodapp.utils.Constants.Companion.SAD_MF
import com.example.moodapp.utils.Constants.Companion.WONDER_MF
import com.example.moodapp.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates

/**
 * Code for floating icon funtionality is adapted from https://drive.google.com/file/d/1fY9r9uNZ9JYcbFWInI3ivmOyZEsMURG_/view
 */

class MoodIconService() : LifecycleService() {

    val TAG = "MoodIconService"
    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var floatingView: View
    private lateinit var sessionManager: SessionManager
    private lateinit var spotifyRepository: SpotifyRepository
    private lateinit var playlistName: String
    private lateinit var trackUri: String
    private lateinit var playlistId: String
    private lateinit var userId: String


    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show()


        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager


        //inflate floating view
        floatingView = LayoutInflater.from(this).inflate(R.layout.service_mood_overlay, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //specify the view position
        params.gravity = Gravity.TOP or Gravity.START

        params.x = 0
        params.y = 100

        //add the view to the window
        windowManager.addView(floatingView, params)

        //set the close button
        val closeButtonCollapsed = floatingView.findViewById(R.id.close_btn) as ImageView
        closeButtonCollapsed.setOnClickListener { stopSelf() }


        val showMfIcon = floatingView.findViewById<View>(R.id.mf_icon_container)


        showMfIcon.setOnTouchListener(object : View.OnTouchListener {


            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0.0f
            private var initialTouchY = 0.0f
            var intent: Intent? = null


            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        var xDiff = (event.rawX - initialTouchX).toInt()
                        var yDiff = (event.rawY - initialTouchY).toInt()

                        //Click event, must check  for Xdiff<10 && Ydiff<10 as sometimes the icon may move a little when clicking
                        //Will show user the mood option tags

                        if (xDiff < 10 && yDiff < 10) {

                            showMoodTags()
                            val moodTagsMinBtn =
                                floatingView.findViewById<ImageView>(R.id.mood_tag_min_btn)

                            //set onclick listener for minimise button in mood tags
                            moodTagsMinBtn.setOnClickListener {
                                showMfIcon()
                            }

                            windowManager.updateViewLayout(floatingView, params)


                        }
                    }


                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //update the layout with new x and y coordinates
                        windowManager.updateViewLayout(floatingView, params)
                    }
                }

                return true
            }


        })

    }


    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) windowManager.removeView(floatingView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /**
     * Show moodfy icon and hide the moodtags
     */
    private fun showMfIcon() {
        val moodTags = floatingView.findViewById<View>(R.id.mood_tags_container)

        val mfIcon = floatingView.findViewById<View>(R.id.mf_icon)
        moodTags.visibility = View.GONE
        mfIcon.visibility = View.VISIBLE

    }

    /**
     * Show mood tags and hide moodfy icon
     */
    private fun showMoodTags() {
        //to get auth token for api calls
        sessionManager = SessionManager(applicationContext)
        //instance of repository to make API requests
        spotifyRepository = SpotifyRepository()


        val moodTags = floatingView.findViewById<View>(R.id.mood_tags_container)
        val mfIcon = floatingView.findViewById<View>(R.id.mf_icon)

        //shows mood tags and hides the Moodfy icon
        moodTags.visibility = View.VISIBLE
        mfIcon.visibility = View.GONE

        //set onClick listeners for the mood tags
        val mood1 = floatingView.findViewById<Button>(R.id.mood_1)
        val mood2 = floatingView.findViewById<Button>(R.id.mood_2)
        val mood3 = floatingView.findViewById<Button>(R.id.mood_3)
        val mood4 = floatingView.findViewById<Button>(R.id.mood_4)
        val mood5 = floatingView.findViewById<Button>(R.id.mood_5)
        val mood6 = floatingView.findViewById<Button>(R.id.mood_6)
        val mood7 = floatingView.findViewById<Button>(R.id.mood_7)
        val mood8 = floatingView.findViewById<Button>(R.id.mood_8)
        val mood9 = floatingView.findViewById<Button>(R.id.mood_9)
        val mood10 = floatingView.findViewById<Button>(R.id.mood_10)


        mood1.setOnClickListener {
            playlistName = HAPPY_MF
            //Toast.makeText(applicationContext, "${sessionManager.fetchAuthToken()}", Toast.LENGTH_SHORT).show()
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
            //getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")
        }

        mood2.setOnClickListener {
            playlistName = SAD_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood3.setOnClickListener {
            playlistName = PRIDE_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }
        mood4.setOnClickListener {
            playlistName = CALM_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood5.setOnClickListener {
            playlistName = EXCITED_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood6.setOnClickListener {
            playlistName = LOVE_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood7.setOnClickListener {
            playlistName = ANGRY_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood8.setOnClickListener {
            playlistName = NOSTALGIC_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood9.setOnClickListener {
            playlistName = WONDER_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood10.setOnClickListener {
            playlistName = AMUSED_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }


    }


    /**
     *
     * Get's user's currently playing track. If successful api response will save track uri then will move on to getUserPlayists function.
     *
     */
    private fun getCurrentTrack(token: String, marketCode: String) = lifecycleScope.launch {
        var isPlayling: Boolean

        val trackResponse = try {
            // Because getCurrentTrack is a suspend function in SpotifyAPI, code will only continue once current track has been retrieved from api
            spotifyRepository.getCurrentTrack(token, marketCode)
        } catch (e: IOException) {
            Log.e(TAG, "IOException, you may not have internet connection")
            return@launch
        } catch (e: HttpException) {
            Log.e(TAG, "HttpException, unexpected response")
            return@launch
        }

        //successful response and the body is not null
        if (trackResponse.isSuccessful && trackResponse.body() != null) {
            //check that is playing a track or podcast etc, not an advert
            var currentTrackItem = trackResponse.body()!!.item
            //check if track is currently playing
            isPlayling = trackResponse.body()!!.is_playing

            if (isPlayling && currentTrackItem != null) {
                trackUri = trackResponse.body()!!.item.uri
                getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")
            } else {
                Toast.makeText(
                    applicationContext,
                    "Can't add to playlist. Try playing music in Spotify before clicking mood icon.",
                    Toast.LENGTH_LONG
                ).show()
            }


        } else {
            Log.e(TAG, "Response not successful")
        }
    }


    /**
     * Function to send a get request to the API which returns with the current
     * user's playlists.
     * Try to send request to API, catch if there are exceptions.
     * If the response is successful check if user already has the specific
     * mood playlist. If they do, add the currently playing song to the playlist.
     * If they don't, create a new playlist with the name of the mood they pressed
     * and add the currently playing song to it.
     *
     */
    private fun getUserPlaylists(token: String) = lifecycleScope.launch {
        // havePlaylist to keep check if user already has an existing mood playlist matching tag
        var havePlaylist = false
        val playlistResponse = try {
            spotifyRepository.getUserPlaylists(token)
        } catch (e: IOException) {
            Log.e(TAG, "IOException, you may not have internet connection")
            return@launch
        } catch (e: HttpException) {
            Log.e(TAG, "HttpException, unexpected response")
            return@launch
        }

        if (playlistResponse.isSuccessful && playlistResponse.body() != null) {
            val items = playlistResponse.body()!!.items
            for (item in items) {
                //Log.d(TAG, "${item.name}")

                if (item.name == playlistName) {
                    havePlaylist = true
                    playlistId = item.id
                }
            }

            if (havePlaylist) {
                //getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
                //1 second delay to make sure getCurrentTrack completes before accessing trackUri
                //delay(500L)
                Log.d(TAG, " have playlist $playlistId")
                // post currently playing song to playlist
                addToMoodPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    playlistId,
                    trackUri
                )
            } else {


                // User doesn't have mood playlist created yet. Have to get user id and then create a new playlist with current song being added to it
                getUserProfile("Bearer ${sessionManager.fetchAuthToken()}")

                // Log.d(TAG, "$userId")
                //Log.d(TAG, "Don't have playlist")
                delay(500L)
                createUserPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    userId,
                    CreatePlaylistBody("Your $playlistName playlist", "$playlistName")
                )

                //get current track
                //getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
                // Delay to wait for playlistId to be instantiated in createUserPlaylist
                delay(500L)
                Log.d(TAG, " created playlist $playlistId")
                //add to newly created playlist
                addToMoodPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    playlistId,
                    trackUri
                )

                //Add custom mood image

                /**

                uploadCustomImage(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    playlistId,
                    "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"
                )**/
            }


        } else {
            Log.e(TAG, "Response not successful")
        }


    }

    /**
     * Function which will add the currently playing song to  mood playlist. TrackUri is acquired from getCurrentTrack
     * api call and playistId from getUserPlaylists api call if the playlist was already created. If user doesn't have specific playlist created
     * yet then the playlistId is acquired from createUserPlaylist api call.
     *
     */
    private fun addToMoodPlaylist(token: String, playlistId: String, trackUri: String) =
        lifecycleScope.launch {
            val addSongToPlaylistResponse = try {
                spotifyRepository.addToMoodPlaylist(token, playlistId, trackUri)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you may not have internet connection")
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launch
            }

            if (addSongToPlaylistResponse.isSuccessful && addSongToPlaylistResponse.body() != null) {
                Toast.makeText(applicationContext, "Added to $playlistName", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Log.e(TAG, "Response not successful")
            }
        }


    /**
     * Function which queries Spotify API for the current user's profile.
     * On successfull response, the userId is retrieved and saved to var. This
     * is required in order to create a user playlist.
     */
    private fun getUserProfile(token: String) = lifecycleScope.launch {
        val userProfileResponse = try {
            spotifyRepository.getUserProfile(token)
        } catch (e: IOException) {
            Log.e(TAG, "IOException, you may not have internet connection")
            return@launch
        } catch (e: HttpException) {
            Log.e(TAG, "HttpException, unexpected response")
            return@launch
        }

        if (userProfileResponse.isSuccessful && userProfileResponse.body() != null) {
            userId = userProfileResponse.body()!!.id

            // create a playlist with mood tag name
        } else {
            Log.e(TAG, "Response not successful")
        }
    }

    /**
     * Function to create a new user playlist. This is function is called when the user selects a
     * mood tag and the corresponding mood playlist has not yet been created.
     * userID was acquired from getUserProfile api call.
     */
    private fun createUserPlaylist(
        token: String,
        userId: String,
        createPlaylistBody: CreatePlaylistBody
    ) =
        lifecycleScope.launch {
            val createPlaylistResponse = try {
                spotifyRepository.createUserPlaylist(token, userId, createPlaylistBody)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you may not have internet connection")
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launch
            }

            if (createPlaylistResponse.isSuccessful && createPlaylistResponse.body() != null) {
                playlistId = createPlaylistResponse.body()!!.id

            } else {
                Log.e(TAG, "Response not successful")
            }
        }

    /**
    private fun uploadCustomImage(token: String, playlistId: String, customImage: String) =
        lifecycleScope.launch {
            val uploadCustomImageResponse = try {
                spotifyRepository.uploadCustomImage(token, playlistId, customImage)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you may not have internet connection")
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launch
            }

            if (uploadCustomImageResponse.isSuccessful) {
                Log.d(TAG, "Image uploaded")
            } else {
                Log.e(TAG, "Response not successful")
            }
        }**/


}









