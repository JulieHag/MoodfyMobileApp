package com.example.moodapp.ui.floatingIcon

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import com.example.moodapp.R


class MoodIconService : Service(), View.OnTouchListener, View.OnClickListener {

    private lateinit var windowManager: WindowManager
    private lateinit var moodOverlayBtn: ImageButton
    private lateinit var params: WindowManager.LayoutParams
    // private lateinit var floatingView: View


    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        moodOverlayBtn = ImageButton(this)
        moodOverlayBtn.setImageResource(R.mipmap.ic_moodfloat_icon)
        moodOverlayBtn.setBackgroundResource(0)
        moodOverlayBtn.setOnTouchListener(this)
        moodOverlayBtn.setOnClickListener(this)

        //inflate floating view
        // floatingView = LayoutInflater.from(this).inflate(R.layout.mood_overlay_layout, null)

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
        windowManager.addView(moodOverlayBtn, params)

        //set the close button
        //val closeButtonCollapsed = floatingView.findViewById(R.id.close_btn) as ImageView
        //closeButtonCollapsed.setOnClickListener { stopSelf() }

        //floatingView.findViewById<View>(R.id.mf_icon)
        // .setOnTouchListener(object : View.OnTouchListener {


        //}

    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(moodOverlayBtn)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f
    private var moving = false

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        view!!.performClick()

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                moving = true
            }
            MotionEvent.ACTION_UP -> {
                moving = false
            }
            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - initialTouchX).toInt()
                params.y = initialY + (event.rawY - initialTouchY).toInt()

                //update the layout with new x and y coordinates
                windowManager.updateViewLayout(moodOverlayBtn, params)
            }
        }

        return true
    }

    override fun onClick(v: View?) {
        if (!moving) Toast.makeText(this, "Button touched", Toast.LENGTH_SHORT).show()
    }


}


