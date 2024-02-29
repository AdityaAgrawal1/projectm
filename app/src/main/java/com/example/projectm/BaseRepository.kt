package com.example.projectm

import android.util.Log
import com.example.neko.utils.resource.Resource
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


abstract class BaseRepository {

    companion object{
        private val TAG : String? = BaseRepository::class.java.canonicalName
    }

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(
                            false,
                            throwable.response()?.message()
                        )
                    }

                    is JsonSyntaxException -> {
                        Log.e(TAG, "safeApiCall: ", throwable)
                        Resource.Failure(false, "")
                    }

                    else -> {
                        Resource.Failure(true, "")
                    }
                }
            }
        }
    }

}