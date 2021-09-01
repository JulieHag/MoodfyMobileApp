package com.example.moodapp.ui.floatingIcon

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.moodapp.R
import com.example.moodapp.data.api.RetrofitInstance
import com.example.moodapp.data.api.SpotifyAPI
import com.example.moodapp.models.currentlyPlaying.CurrentTrackResponse
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.utils.Resource
import com.example.moodapp.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Code for floating icon funtionality is adapted from https://drive.google.com/file/d/1fY9r9uNZ9JYcbFWInI3ivmOyZEsMURG_/view
 */
class MoodIconService() : LifecycleService() {


    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var floatingView: View
    private lateinit var sessionManager: SessionManager
    private lateinit var spotifyRepository: SpotifyRepository
    val currentTrack: MutableLiveData<Resource<CurrentTrackResponse>> = MutableLiveData()
    val TAG = "MoodIconService"


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
        val moodTags = floatingView.findViewById<View>(R.id.mood_tags_container)

        val mfIcon = floatingView.findViewById<View>(R.id.mf_icon)

        moodTags.visibility = View.VISIBLE
        mfIcon.visibility = View.GONE

        //set onClick listeners for the mood tags
        val mood1 = floatingView.findViewById<ImageButton>(R.id.mood_1)
        val mood2 = floatingView.findViewById<ImageButton>(R.id.mood_2)


        mood1.setOnClickListener {
           sessionManager = SessionManager(applicationContext)
            spotifyRepository = SpotifyRepository()


             //Toast.makeText(applicationContext, "${sessionManager.fetchAuthToken()}", Toast.LENGTH_SHORT).show()
            getCurrentTrack("Bearer ${sessionManager.fetchAuthToken()}", "GB")
        }

        mood2.setOnClickListener{

        }


    }

    fun getCurrentTrack(token: String, marketCode: String) = lifecycleScope.launch {
        //currentTrack.postValue(Resource.Loading())
        val response = spotifyRepository.getCurrentTrack(token, marketCode)
        currentTrack.postValue(handleCurrentTrackResponse(response))
        currentTrack.observe(this@MoodIconService, Observer { response ->
            when (response) {
                is Resource.Success -> {

                    val trackUri = response.data?.item?.uri
                    Toast.makeText(applicationContext, trackUri.toString(), Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }

                }

            }
        })

    }

    private fun handleCurrentTrackResponse(response: Response<CurrentTrackResponse>): Resource<CurrentTrackResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}






