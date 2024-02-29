package com.example.neko.utils.resource

import okhttp3.ResponseBody

sealed class Resource<out T> {

    data class Success<out T>(val value: T) : Resource<T>()

    data class Failure(
        val isNetworkError: Boolean,
        val errorMsg: String?
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}