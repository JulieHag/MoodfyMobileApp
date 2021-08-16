package com.example.moodapp.ui.floatingIcon

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.example.moodapp.MainActivity
import com.example.moodapp.R


class MoodIconService : Service() {


    private lateinit var windowManager: WindowManager

    //private lateinit var moodOverlayBtn: ImageButton
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var floatingView: View


    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        /**
        moodOverlayBtn = ImageButton(this)
        moodOverlayBtn.setImageResource(R.mipmap.ic_moodfloat_icon)
        moodOverlayBtn.setBackgroundResource(0)
        moodOverlayBtn.setOnTouchListener(this)
        moodOverlayBtn.setOnClickListener(this)
         **/
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

        floatingView.findViewById<View>(R.id.mf_icon)
            .setOnTouchListener(object : View.OnTouchListener {

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
                            if(xDiff<10 && yDiff<10){
                               intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                //This part doesn't work yet
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
    override fun onClick(v: View?) {
    if (!moving) Toast.makeText(this, "Button touched", Toast.LENGTH_SHORT).show()
    }**/


}


