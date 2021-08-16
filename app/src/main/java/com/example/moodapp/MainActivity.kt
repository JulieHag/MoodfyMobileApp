package com.example.moodapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moodapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


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


        var canDraw = true
        var intent: Intent? = null

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            canDraw = Settings.canDrawOverlays(this)
            if(!canDraw && intent != null){
                startActivity(intent)
            }
        }

        /**
        val startMoodfyBtn = findViewById<Button>(R.id.start_moodfy_btn)
         startMoodfyBtn.setOnClickListener {
             val service = Intent(this, MoodIconService::class.java)
             startService(service)

         }

        val stopMoodfyBtn = findViewById<Button>(R.id.stop_moodfy_btn)
        stopMoodfyBtn.setOnClickListener{
            val service = Intent(this, MoodIconService::class.java)
            stopService(service)
        } **/


         }
    }


