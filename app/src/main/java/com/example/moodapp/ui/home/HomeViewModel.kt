package com.example.moodapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to Moodfy. Where moods and music meet." +
                " Making playlists based on moods just became that much easier!" +
                " To get started simply hit START MOODFYING YOUR MUSIC and" +
                " the Moodfy bubble will allow you yo listen to music in Spotify and easily create mood playlists " +
                " at the click of a button"
    }
    val text: LiveData<String> = _text
}