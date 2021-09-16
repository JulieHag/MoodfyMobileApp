package com.jhag.moodapp.utils

/**
 * Sealed class - Can define which classes are allowed to inherit from Resource
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    //data not nullable here because if successful then body cannot be null
    class Success<T>(data: T) : Resource<T>(data)

    //data nullable here because can have a body in an error response but not always
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    //loading state
    class Loading<T> : Resource<T>()

}

