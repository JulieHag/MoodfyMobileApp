package com.jhag.moodapp.data.api

import com.jhag.moodapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Code adapted from https://github.com/philipplackner/MVVMNewsApp/blob/Retrofit-setup/app/src/main/java/com/androiddevs/mvvmnewsapp/api/RetrofitInstance.kt
 * Retrofit singleton class which allows us to make requests from everywhere in code
 * Lazy means that it will only be initialised once
 */
class RetrofitInstance {


    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        // api object that will be used to make requests
        val api by lazy {
            retrofit.create(SpotifyAPI::class.java)
        }



    }



}

/**
//Initialise OkhttpClient with our interceptors

private fun okhttpClient(context: Context): OkHttpClient {
val logging = HttpLoggingInterceptor()
logging.setLevel(HttpLoggingInterceptor.Level.BODY)
return OkHttpClient.Builder()
.addInterceptor(AuthInterceptor(context))
.addInterceptor(logging)
.build()
}**/