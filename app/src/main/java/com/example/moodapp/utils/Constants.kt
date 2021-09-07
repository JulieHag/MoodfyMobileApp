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
        const val NOSTALGIC_MF = "NOSTALGIC_MF"
        const val LOVE_MF = "LOVE_MF"
        const val EXCITED_MF = "EXCITED_MF"
        const val CALM_MF = "CALM_MF"
        const val PRIDE_MF = "PRIDE_MF"
        const val ANGRY_MF = "ANGRY_MF"
        const val WONDER_MF = "WONDER_MF"
        const val AMUSED_MF = "AMUSED_MF"

        //constants for notification channel
        const val NOTIFICATION_CHANNEL_ID = "moodfy_channel"
        const val NOTIFICATION_CHANNEL_NAME ="Moodfy"
        const val NOTIFICATION_ID = 1




    }
}