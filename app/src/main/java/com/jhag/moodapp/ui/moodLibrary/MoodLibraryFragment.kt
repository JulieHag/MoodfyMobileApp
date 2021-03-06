package com.jhag.moodapp.ui.moodLibrary

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jhag.moodapp.adapters.PlaylistAdapter
import com.jhag.moodapp.data.models.userPlaylists.Item
import com.jhag.moodapp.databinding.FragmentMoodLibraryBinding
import com.jhag.moodapp.repository.SpotifyRepository
import com.jhag.moodapp.ui.viewmodels.MoodLibraryViewModel
import com.jhag.moodapp.ui.viewmodels.MoodLibraryViewModelProviderFactory
import com.jhag.moodapp.utils.Constants
import com.jhag.moodapp.utils.Constants.Companion.AMUSED_MF
import com.jhag.moodapp.utils.Constants.Companion.ANGRY_MF
import com.jhag.moodapp.utils.Constants.Companion.CALM_MF
import com.jhag.moodapp.utils.Constants.Companion.EXCITED_MF
import com.jhag.moodapp.utils.Constants.Companion.HAPPY_MF
import com.jhag.moodapp.utils.Constants.Companion.LOVE_MF
import com.jhag.moodapp.utils.Constants.Companion.NOSTALGIC_MF
import com.jhag.moodapp.utils.Constants.Companion.PRIDE_MF
import com.jhag.moodapp.utils.Constants.Companion.SAD_MF
import com.jhag.moodapp.utils.Constants.Companion.WONDER_MF
import com.jhag.moodapp.utils.Resource
import com.jhag.moodapp.utils.SessionManager
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse


class MoodLibraryFragment : Fragment() {
    val TAG = "MoodLibraryFragment"
    private lateinit var moodLibraryViewModel: MoodLibraryViewModel
    lateinit var playlistAdapter: PlaylistAdapter
    private var _binding: FragmentMoodLibraryBinding? = null
    private lateinit var sessionManager: SessionManager

    val positiveButtonClick = { _: DialogInterface, _: Int ->

        spotifyAccess()
    }

    val negativeButtonClick = { _: DialogInterface, _: Int ->
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        hideProgressBar()

    }


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val spotifyRepository = SpotifyRepository()
        val viewModelProviderFactory =
            MoodLibraryViewModelProviderFactory(spotifyRepository, requireActivity().application)
        moodLibraryViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MoodLibraryViewModel::class.java)

        _binding = FragmentMoodLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val moodPlaylist = mutableListOf<Item>()
        val textView: TextView = binding.textMoodLibrary
        sessionManager = SessionManager(requireContext())

        if (sessionManager.fetchAuthToken() == null) {
            spotifyLibraryAccessAlert()
        }


        /**
         * On click listener - takes the playlist item passed from PlaylstAdapter
         * and then it.uri accesses the playlist uri of the clicked item.
         * Passes this uri as an argument to linkToSpotify.
         */
        playlistAdapter.setOnItemClickListener {
            var playlistUri = it.uri
            linkToSpotify(playlistUri)
        }

        moodLibraryViewModel.errorStatus.observe(viewLifecycleOwner, Observer { errorStat ->

            errorStat?.let {
                hideProgressBar()
                moodLibraryViewModel.errorStatus.value = null
                Toast.makeText(
                    context,
                    "Error. Check internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

        })

        moodLibraryViewModel.userPlaylist.observe(viewLifecycleOwner, Observer { playlistResponse ->
            when (playlistResponse) {
                is Resource.Success -> {
                    hideProgressBar()
                    val items = playlistResponse.data?.items

                    if (items != null) {
                        for (item in items) {

                            if (item.name == HAPPY_MF || item.name == SAD_MF || item.name == PRIDE_MF || item.name == CALM_MF
                                || item.name == EXCITED_MF || item.name == LOVE_MF || item.name == ANGRY_MF || item.name == NOSTALGIC_MF
                                || item.name == WONDER_MF || item.name == AMUSED_MF
                            ) {
                                //Log.d(TAG, "${item.name}")
                                moodPlaylist.add(item)
                            }
                        }
                        playlistAdapter.differ.submitList(moodPlaylist)

                        /**
                         * Shows message in textview if the user doesn't have any saved playlists
                         * yet. It will disappear as soon as the user creates a moodfy playlist.
                         */
                        if (moodPlaylist.isEmpty()) {
                            moodLibraryViewModel.text.observe(viewLifecycleOwner, Observer {
                                textView.text = it
                            })
                            textView.visibility = View.VISIBLE
                        } else {
                            textView.visibility = View.GONE
                        }
                    }
                }
                is Resource.Error -> {
                    sessionManager.clearPrefs()
                    playlistResponse.message?.let { message ->
                        Log.e(TAG, "An error has occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()

                }

            }


        })


    }

    /**
     * Function to show progress bar
     */
    private fun showProgressBar() {
        binding.moodLibraryProgress.visibility = View.VISIBLE
    }

    /**
     * Function to hide progress bar
     */
    private fun hideProgressBar() {
        binding.moodLibraryProgress.visibility = View.INVISIBLE
    }

//onclick listener for btn
//binding.goToSpotify.setOnClickListener {linkToSpotify()}

    /**
     * Function to set up recycler view
     */
    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter()
        binding.rvPlaylists.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }

    /**
     * Code adapted from spotify developeent guides. Found at:
     * https://developer.spotify.com/documentation/general/guides/content-linking-guide/
     * Checks if spotify app is installed on device.
     * If it is then the user will be sent to spotify app by using the Uri passed from the on click
     *  listener. This will send the user to that specific mood playlist on spotify.
     */
    fun linkToSpotify(playlistUri: String) {

        val pm: PackageManager = context?.packageManager!!
        val isSpotifyInstalled: Boolean = try {
            pm.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if (isSpotifyInstalled) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(playlistUri)
            intent.putExtra(
                Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + requireContext().packageName)
            )
            startActivity(intent)
        } else {
            Toast.makeText(
                requireContext(),
                "Please install Spotify from google play to open playlist.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun spotifyLibraryAccessAlert() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Permission required")
            setMessage("To use this feature of the app you must allow the app access to your Spotify account. Click OK to allow access.")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick)
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
