package com.example.moodapp.utils

/**
 * Class to wrap around network responses
 * Helps to differentiate between successful and error responses
 * Helps handle loading state
 * Generic class
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): Resource<T>(data)
    //data set to null and is nullable because sometimes will have an error response with no body
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()

}