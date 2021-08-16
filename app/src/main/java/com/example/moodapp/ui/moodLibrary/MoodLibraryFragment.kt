package com.example.moodapp.ui.moodLibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moodapp.databinding.FragmentMoodLibraryBinding

class MoodLibraryFragment : Fragment() {

    private lateinit var moodLibraryViewModel: MoodLibraryViewModel
    private var _binding: FragmentMoodLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moodLibraryViewModel =
            ViewModelProvider(this).get(MoodLibraryViewModel::class.java)

        _binding = FragmentMoodLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMoodLibrary
        moodLibraryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}