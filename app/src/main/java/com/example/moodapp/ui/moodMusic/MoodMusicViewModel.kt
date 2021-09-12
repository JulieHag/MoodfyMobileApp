package com.example.moodapp.ui.moodMusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoodMusicViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Tap to start Moodfying your music"
    }
    val text: LiveData<String> = _text



}