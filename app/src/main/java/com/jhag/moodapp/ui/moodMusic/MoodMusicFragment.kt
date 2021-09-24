package com.jhag.moodapp.ui.moodMusic

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jhag.moodapp.databinding.FragmentMoodMusicBinding
import com.jhag.moodapp.ui.floatingIcon.MoodIconService
import com.jhag.moodapp.utils.Constants
import com.jhag.moodapp.utils.SessionManager
import com.jhag.moodapp.ui.viewmodels.MoodMusicViewModel
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse


class MoodMusicFragment : Fragment() {


    val TAG = "MoodMusicFragment"
    private lateinit var moodMusicViewModel: MoodMusicViewModel
    private var _binding: FragmentMoodMusicBinding? = null
    private lateinit var sessionManager: SessionManager

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

    val positiveButtonClick2= { _: DialogInterface, _: Int ->

        spotifyAccess()
    }

    val negativeButtonClick = { _: DialogInterface, _: Int ->
        Toast.makeText(requireContext(),"Permission denied", Toast.LENGTH_SHORT).show()

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
        sessionManager = SessionManager(requireContext())



        if(sessionManager.fetchAuthToken() == null){
           spotifyAccessAlert()
        } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                canDraw = Settings.canDrawOverlays(requireContext())
                if (!canDraw && intent != null) {
                    permissionAlert()

                } else {
                    requireActivity().startService(Intent(context, MoodIconService::class.java))
                }
        }


        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    /**
     * Code to show user alert informing them that they need to allow overlay permissions
     * code adapted from https://www.journaldev.com/309/android-alert-dialog-using-kotlin
     */
    fun permissionAlert() {


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

    fun spotifyAccessAlert() {


        val builder = AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Permission required")
            setMessage("To use this feature of the app you must allow the app access to your Spotify account. Click OK to allow access.")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick2)
            )
            setNegativeButton("Cancel", negativeButtonClick)
            show()
        }

    }


    /**
     * Asks user for permission to access their spotify
     */
    fun spotifyAccess() {

        // code adapted from spotify authentication guide
        val builder = AuthenticationRequest.Builder(
            Constants.CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            Constants.REDIRECT_URI
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
        AuthenticationClient.openLoginActivity(requireActivity(), Constants.REQUEST_CODE, request)


    }


}

