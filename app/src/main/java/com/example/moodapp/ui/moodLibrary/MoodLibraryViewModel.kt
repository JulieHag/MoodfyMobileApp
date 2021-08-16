package com.example.moodapp.ui.moodLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoodLibraryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is mood library Fragment"
    }
    val text: LiveData<String> = _text
}