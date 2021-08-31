package com.example.moodapp.data

import android.content.Context
import com.example.moodapp.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Interceptor to add AUTH_TOKEN to requests
 */
class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // if token has been saved add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorisation", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }



}