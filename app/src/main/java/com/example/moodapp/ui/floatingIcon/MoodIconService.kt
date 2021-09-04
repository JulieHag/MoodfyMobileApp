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
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.utils.Constants.Companion.HAPPY_MF
import com.example.moodapp.utils.Constants.Companion.SAD_MF
import com.example.moodapp.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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


        mood1.setOnClickListener {
            playlistName = HAPPY_MF
            //Toast.makeText(applicationContext, "${sessionManager.fetchAuthToken()}", Toast.LENGTH_SHORT).show()

            getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")


        }

        mood2.setOnClickListener {
            playlistName = SAD_MF
            getUserPlaylists("Bearer ${sessionManager.fetchAuthToken()}")

        }


    }

    /**
    private fun getCurrentTrack2(token: String, marketCode: String) = lifecycleScope.launch {
    //currentTrack.postValue(Resource.Loading())
    val response = spotifyRepository.getCurrentTrack(token, marketCode)
    // handle the current track response and return whether the network call  has been successful or not
    currentTrack.postValue(handleCurrentTrackResponse(response))
    //observe the live data and carry out actions depending on the return object from handleCurrentTrackResponse (i.e. successful or error)
    currentTrack.observe(this@MoodIconService, Observer { response ->
    when (response) {
    is Resource.Success -> {

    //val trackUri = response.data?.item?.uri
    // Toast.makeText(applicationContext, trackUri.toString(), Toast.LENGTH_SHORT)
    //.show()
    }
    is Resource.Error -> {
    response.message?.let { message ->
    Log.e(TAG, "An error occured: $message")
    }

    }

    }
    })

    }**/


    /**
     *
     */
    private fun getCurrentTrack(token: String, marketCode: String) = lifecycleScope.launch {

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

        if (trackResponse.isSuccessful && trackResponse.body() != null) {
            //successful response and the body is not null
            trackUri = trackResponse.body()!!.item.uri

            //Toast.makeText(applicationContext, "hello", Toast.LENGTH_SHORT)


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
                getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
                //1 second delay to make sure getCurrentTrack completes before accessing trackUri
                delay(1000L)
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
                delay(1000L)
               // Log.d(TAG, "$userId")
                Log.d(TAG, "Don't have playlist")
                createUserPlaylist("Bearer ${sessionManager.fetchAuthToken()}", userId, "name: $playlistName")
            }


        } else {
            Log.e(TAG, "Response not successful")
        }


    }

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

    private fun createUserPlaylist(token: String, userId: String, name: String) =
        lifecycleScope.launch {
            val createPlaylistResponse = try {
                spotifyRepository.createUserPlaylist(token, userId, name)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you may not have internet connection")
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launch
            }

            if(createPlaylistResponse.isSuccessful && createPlaylistResponse.body() != null){
                Log.d(TAG, "Success! Created new playlist")
            } else {
                Log.e(TAG, "Response not successful")
            }
        }


}









