package com.example.moodapp.ui.moodLibrary

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodapp.adapters.PlaylistAdapter
import com.example.moodapp.databinding.FragmentMoodLibraryBinding
import com.example.moodapp.models.userPlaylists.Item
import com.example.moodapp.repository.SpotifyRepository
import com.example.moodapp.ui.SpotifyViewModelProviderFactory
import com.example.moodapp.utils.Constants.Companion.AMUSED_MF
import com.example.moodapp.utils.Constants.Companion.ANGRY_MF
import com.example.moodapp.utils.Constants.Companion.CALM_MF
import com.example.moodapp.utils.Constants.Companion.EXCITED_MF
import com.example.moodapp.utils.Constants.Companion.HAPPY_MF
import com.example.moodapp.utils.Constants.Companion.LOVE_MF
import com.example.moodapp.utils.Constants.Companion.NOSTALGIC_MF
import com.example.moodapp.utils.Constants.Companion.PRIDE_MF
import com.example.moodapp.utils.Constants.Companion.SAD_MF
import com.example.moodapp.utils.Constants.Companion.WONDER_MF
import com.example.moodapp.utils.Resource


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
            SpotifyViewModelProviderFactory(spotifyRepository, requireActivity().application)
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

        playlistAdapter.setOnItemClickListener {
            linkToSpotify()
        }

        moodLibraryViewModel.userPlaylist.observe(viewLifecycleOwner, Observer { playlistResponse ->
            when (playlistResponse) {
                is Resource.Success -> {
                    val items = playlistResponse.data?.items

                    if (items != null) {
                        for (item in items) {

                            if (item.name == HAPPY_MF || item.name == SAD_MF || item.name == PRIDE_MF || item.name == CALM_MF
                                || item.name == EXCITED_MF || item.name == LOVE_MF || item.name == ANGRY_MF || item.name == NOSTALGIC_MF
                                || item.name == WONDER_MF || item.name == AMUSED_MF) {

                                Log.d(TAG, "${item.name}")
                                moodPlaylist.add(item)

                            }

                        }

                        playlistAdapter.differ.submitList(moodPlaylist)
                    }
                }
                is Resource.Error -> {
                    playlistResponse.message?.let { message ->
                        Log.e(TAG, "An error has occurred: $message")
                    }
                }

            }


        })


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

    fun linkToSpotify() {
        /**
         * Code adapted from spotify developeent guides. Found at:
         * https://developer.spotify.com/documentation/general/guides/content-linking-guide/
         * Checks if spotify app is installed on device
         * If it is then the user will be sent to spotify app by using the Uri to specify
         * which section of spotify the user shall be sent to.
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
            intent.data = Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj")
            intent.putExtra(
                Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + requireContext().packageName)
            )
            startActivity(intent)
        } else {
            Toast.makeText(
                requireContext(),
                "Please install Spotify from google play.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
