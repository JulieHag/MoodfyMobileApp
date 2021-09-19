package com.jhag.moodapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.jhag.moodapp.R

/**
 * Session manager in order to allow user to fetch and save authentication token after successful authentication request
 * code adapted from https://medium.com/android-news/token-authorization-with-retrofit-android-oauth-2-0-747995c79720
 */
class SessionManager (context: Context) {
    //shared preferences mode private as this means the created file can only be accessed by the calling application
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)


    companion object{
        const val AUTH_TOKEN = "AUTH_TOKEN"
    }

    /**
     *  Function to save auth token
     */
    fun saveAuthToken(token: String){
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    /**
     *
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String?{
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun clearPrefs(){
        prefs.edit().clear().apply()

    }


}