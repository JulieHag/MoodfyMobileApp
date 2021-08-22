package com.example.moodapp

import android.os.Bundle
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





    }


/** fun basicAlert() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Permission required")
            setMessage("This is message")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", negativeButtonClick)
            show()
        }
    }


    private fun setupPermissions() {
    val permission =
    ContextCompat.checkSelfPermission(this, android.Manifest.permission.SYSTEM_ALERT_WINDOW)

    if (permission != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
    this,
    Manifest.permission.SYSTEM_ALERT_WINDOW)) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Overlay permission is required")
    builder.setTitle("Permission required")
    builder.setPositiveButton("OK"){
    dialog, id -> makeRequest()
    }
    val dialog = builder.create()
    dialog.show()
    } else {
    makeRequest()
    }
    }
    }


    private fun makeRequest() {
    ActivityCompat.requestPermissions(
    this,
    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
    REQUEST_CODE_OVERLAY_PERMISSION
    )

    var canDraw = true
    var intent: Intent? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    canDraw = Settings.canDrawOverlays(this)
    if (!canDraw && intent != null) {
    startActivity(intent)
    }
    }


    }

    override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
    ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    } **/


}


