package com.jhag.moodapp.ui.floatingIcon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jhag.moodapp.R
import com.jhag.moodapp.data.models.createPlaylistBody.CreatePlaylistBody
import com.jhag.moodapp.repository.SpotifyRepository
import com.jhag.moodapp.utils.Constants.Companion.AMUSED_MF
import com.jhag.moodapp.utils.Constants.Companion.ANGRY_MF
import com.jhag.moodapp.utils.Constants.Companion.CALM_MF
import com.jhag.moodapp.utils.Constants.Companion.EXCITED_MF
import com.jhag.moodapp.utils.Constants.Companion.HAPPY_MF
import com.jhag.moodapp.utils.Constants.Companion.LOVE_MF
import com.jhag.moodapp.utils.Constants.Companion.NOSTALGIC_MF
import com.jhag.moodapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.jhag.moodapp.utils.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.jhag.moodapp.utils.Constants.Companion.NOTIFICATION_ID
import com.jhag.moodapp.utils.Constants.Companion.PRIDE_MF
import com.jhag.moodapp.utils.Constants.Companion.SAD_MF
import com.jhag.moodapp.utils.Constants.Companion.WONDER_MF
import com.jhag.moodapp.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


/**
 * Made service foreground service so that it can't be killed by android system
 * extends LifecycleService to have access to lifecycleScope which is used to start coroutines
 * that will automatically be cancelled when the Service is stopped (for network calls).
 */

class MoodIconService : LifecycleService() {

    //for debug log
    val TAG = "MoodIconService"

    //vars
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

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //inflate floating view
        floatingView = LayoutInflater.from(this).inflate(R.layout.service_mood_overlay, null)

        startForegroundService()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            //for older phones
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

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Make service foreground service so that android system can't kill service
     */
    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            //always want notification to be active
            .setAutoCancel(false)
            .setOngoing(true) //notification can't be swiped away
            .setSmallIcon(R.drawable.ic_mf_tab)
            .setContentTitle("Moodfy App")
            .setContentText("Moodfying your music")

        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    /**
     * Notification channel for foreground service
     * IMORTANCE_LOW so that notification doesn't come with a sound
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
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
        val moodHappy = floatingView.findViewById<Button>(R.id.mood_happy)
        val moodSad = floatingView.findViewById<Button>(R.id.mood_sad)
        val moodPride = floatingView.findViewById<Button>(R.id.mood_pride)
        val moodCalm = floatingView.findViewById<Button>(R.id.mood_calm)
        val moodExcited = floatingView.findViewById<Button>(R.id.mood_excited)
        val moodLove = floatingView.findViewById<Button>(R.id.mood_love)
        val moodAngry = floatingView.findViewById<Button>(R.id.mood_angry)
        val moodNostalgic = floatingView.findViewById<Button>(R.id.mood_nostalgic)
        val moodWonder = floatingView.findViewById<Button>(R.id.mood_wonder)
        val moodAmused = floatingView.findViewById<Button>(R.id.mood_amused)


        moodHappy.setOnClickListener {
            playlistName = HAPPY_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodSad.setOnClickListener {
            playlistName = SAD_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodPride.setOnClickListener {
            playlistName = PRIDE_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }
        moodCalm.setOnClickListener {
            playlistName = CALM_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodExcited.setOnClickListener {
            playlistName = EXCITED_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodLove.setOnClickListener {
            playlistName = LOVE_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodAngry.setOnClickListener {
            playlistName = ANGRY_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodNostalgic.setOnClickListener {
            playlistName = NOSTALGIC_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodWonder.setOnClickListener {
            playlistName = WONDER_MF
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        moodAmused.setOnClickListener {
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
        var isPlaying: Boolean
        val trackResponse = try {
            // Because getCurrentTrack is a suspend function in SpotifyAPI,
                // code will only continue once current track has been retrieved from api
            spotifyRepository.getCurrentTrack(token, marketCode)
        } catch (e: IOException) {
            Log.e(TAG, "IOException, you may not have internet connection")
            Toast.makeText(
                applicationContext,
                "Error. Check internet connection",
                Toast.LENGTH_LONG
            ).show()
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
            isPlaying = trackResponse.body()!!.is_playing

            if (isPlaying && currentTrackItem != null) {
                trackUri = trackResponse.body()!!.item.uri
                getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")
            } else {
                Toast.makeText(
                    applicationContext,
                    "Can't add to playlist. Make sure music is playing in Spotify before clicking mood icon.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (trackResponse.code() == 401) {
            sessionManager.clearPrefs()
            Toast.makeText(
                applicationContext,
                "Something went wrong. Try opening Moodfy then retry.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Can't add to playlist. Make sure music is playing in Spotify before clicking mood icon.",
                Toast.LENGTH_LONG
            ).show()
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

                if (item.name == playlistName) {
                    havePlaylist = true
                    playlistId = item.id
                }
            }
            if (havePlaylist) {

                // post currently playing song to playlist
                addToMoodPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    playlistId,
                    trackUri
                )
            } else {
                // User doesn't have mood playlist created yet. Have to get user id and then create
                    // a new playlist with current song being added to it
                getUserProfile("Bearer ${sessionManager.fetchAuthToken()}")

                delay(500L)
                createUserPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    userId,
                    CreatePlaylistBody("Your $playlistName playlist", playlistName)
                )
                //get current track
                // Delay to wait for playlistId to be instantiated in createUserPlaylist
                delay(500L)

                //add to newly created playlist
                addToMoodPlaylist(
                    "Bearer ${sessionManager.fetchAuthToken()}",
                    playlistId,
                    trackUri
                )
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
     * On successful response, the userId is retrieved and saved to var. This
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

}









