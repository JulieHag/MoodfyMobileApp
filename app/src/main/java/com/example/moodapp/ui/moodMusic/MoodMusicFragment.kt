package com.example.moodapp.ui.moodMusic

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moodapp.databinding.FragmentMoodMusicBinding
import com.example.moodapp.ui.floatingIcon.MoodIconService


class MoodMusicFragment : Fragment() {


    private lateinit var moodMusicViewModel: MoodMusicViewModel
    private var _binding: FragmentMoodMusicBinding? = null

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
        })
        return root


        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //onclick listener for start service button

        binding.startMoodfyBtn.setOnClickListener{startMoodService()}

        //onclick listener to stop the service
        binding.stopMoodfyBtn.setOnClickListener{stopMoodService()}

    }

    fun startMoodService(){
        requireActivity().startService(Intent(context, MoodIconService::class.java))
    }

    fun stopMoodService(){
        requireActivity().stopService(Intent(context, MoodIconService::class.java))
    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


    }