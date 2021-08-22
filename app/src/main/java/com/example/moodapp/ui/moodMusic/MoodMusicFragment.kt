package com.example.moodapp.ui.moodMusic

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moodapp.databinding.FragmentMoodMusicBinding
import com.example.moodapp.ui.floatingIcon.MoodIconService


class MoodMusicFragment : Fragment() {


    private lateinit var moodMusicViewModel: MoodMusicViewModel
    private var _binding: FragmentMoodMusicBinding? = null

    val positiveButtonClick = { _: DialogInterface, _: Int ->

        var canDraw = true
        var intent: Intent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            //canDraw checks whether user has already allowed the overlay permission
            canDraw = Settings.canDrawOverlays(requireContext())
            if (!canDraw && intent != null) {
                startActivity(intent)
            }
        }


    }

    val negativeButtonClick = { _: DialogInterface, _: Int ->

    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moodMusicViewModel =
            ViewModelProvider(this).get(MoodMusicViewModel::class.java)

        _binding = FragmentMoodMusicBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMoodMusic
        moodMusicViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it

            //requestPermissions()
        })
        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //onclick listener for start service button

        binding.startMoodfyBtn.setOnClickListener { startMoodService() }


    }

    /**
     * if user's device is running on marshmallow OS or later then they will have to accept overlay permissions
     * to allow app to draw over other apps
     * only start service if the user has allowed the overlay permission in phone settings
     * var canDraw is used to check if user has already accepted the overlay permissions
     * if they have not then the user will be shown an alery informing them that they need to allow this
     * in their settings
     */

    fun startMoodService() {
        var canDraw = true
        var intent: Intent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            canDraw = Settings.canDrawOverlays(requireContext())
            if (!canDraw && intent != null) {
                basicAlert()
                //startActivity(intent)
            } else {
                requireActivity().startService(Intent(context, MoodIconService::class.java))
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**private fun requestPermission(){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
    val builder = AlertDialog.Builder(requireContext())
    builder.setTitle("Permission required"),
    builder.setMessage("To use this feature of the app you must allow it to display over other apps. Enable this manually in your phone's settings")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->  }){

    var intent: Intent? = null
    intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package", "packageName"))
    }
    }
    }**/

    /**private fun requestPermissions(){
    if(OverlayUtility.hasOverlayPermission(requireContext())){
    return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
    EasyPermissions.requestPermissions(
    this,
    "Permission to allow display over other apps is required for the Moodfy bubble to work on top of other apps",
    Constants.REQUEST_CODE_OVERLAY_PERMISSION,
    android.Manifest.permission.SYSTEM_ALERT_WINDOW
    )
    }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
    AppSettingsDialog.Builder(this).build().show()
    } else{
    requestPermissions()
    }
    }

    //redirect parameters to EasyPermissions so that it has access
    override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
    ) {
    // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }**/


    /**
     * Code to show user alert informing them that they need to allow overlay permissions
     * code adapted from https://www.journaldev.com/309/android-alert-dialog-using-kotlin
     */
    fun basicAlert() {


        val builder = AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Permission required")
            setMessage("To use this feature of the app you must allow it to display over other apps. Enable this manually in your phone's settings")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Cancel", negativeButtonClick)
            show()
        }

    }
}

