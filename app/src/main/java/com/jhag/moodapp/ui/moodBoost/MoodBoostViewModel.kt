package com.jhag.moodapp.ui.moodBoost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoodBoostViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is mood boost Fragment"
    }
    val text: LiveData<String> = _text
}