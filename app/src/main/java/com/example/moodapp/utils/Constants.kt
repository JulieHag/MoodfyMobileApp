package com.example.moodapp.utils

class Constants {

    // companion objet so that we don't need to create an instance of the class
   companion object {
        //used to verify if result comes from the login activity
       const val REQUEST_CODE = 1247
        const val REDIRECT_URI = "com.example.moodapp://callback"
        const val CLIENT_ID = "4ae600c0f0204a4cab8cd4b4bf982119"
        const val BASE_URL = "https://api.spotify.com"

        // playlist names
        const val HAPPY_MF = "HAPPY_MF"
        const val SAD_MF = "SAD_MF"
   }
}