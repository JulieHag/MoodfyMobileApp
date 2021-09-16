package com.jhag.moodapp.ui.moodBoost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jhag.moodapp.databinding.FragmentMoodBoostBinding

class MoodBoostFragment : Fragment() {

    private lateinit var moodBoostViewModel: MoodBoostViewModel
    private var _binding: FragmentMoodBoostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moodBoostViewModel =
            ViewModelProvider(this).get(MoodBoostViewModel::class.java)

        _binding = FragmentMoodBoostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMoodBoost
        moodBoostViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}