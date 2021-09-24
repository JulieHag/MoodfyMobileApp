package com.jhag.moodapp.ui.moodLibrary

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jhag.moodapp.adapters.PlaylistAdapter
import com.jhag.moodapp.data.models.userPlaylists.Item
import com.jhag.moodapp.databinding.FragmentMoodLibraryBinding
import com.jhag.moodapp.repository.SpotifyRepository
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
import com.jhag.moodapp.viewmodels.MoodLibraryViewModel
import com.jhag.moodapp.viewmodels.MoodLibraryViewModelProviderFactory


class MoodLibraryFragment : Fragment() {
    val TAG = "MoodLibraryFragment"
    private lateinit var moodLibraryViewModel: MoodLibraryViewModel
    lateinit var playlistAdapter: PlaylistAdapter
    private var _binding: FragmentMoodLibraryBinding? = null


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


        /**
         * On click listener - takes the playlist item passed from PlaylstAdapter
         * and then it.uri accesses the playlist uri of the clicked item.
         * Passes this uri as an argument to linkToSpotify.
         */
        playlistAdapter.setOnItemClickListener {
            var playlistUri = it.uri
            linkToSpotify(playlistUri)
        }

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
                    playlistResponse.message?.let { message ->
                        Log.e(TAG, "An error has occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()

                }

            }




        })

        /**
         * Shows message in textview if the user doesn't have any saved playlists
         * yet. It will disappear as soon as the user creates a moodfy playlist.
         */




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

    fun linkToSpotify(playlistUri: String) {
        /**
         * Code adapted from spotify developeent guides. Found at:
         * https://developer.spotify.com/documentation/general/guides/content-linking-guide/
         * Checks if spotify app is installed on device.
         * If it is then the user will be sent to spotify app by using the Uri passed from the on click
         *  listener. This will send the user to that specific mood playlist on spotify.
         */

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


}
