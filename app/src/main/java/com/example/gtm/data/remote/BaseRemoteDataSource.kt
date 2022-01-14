package com.example.gtm.data.remote

import com.example.gtm.utils.resources.Resource
import retrofit2.Response
import timber.log.Timber

abstract class BaseRemoteDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.success(body,response.code())
            }
            return error(" ${response.code()} ${response.message()}",response.code())
        } catch (e: Exception) {
            return error(e.message ?: e.toString(),100)
        }
    }

    private fun <T> error(message: String,responseCode:Int?): Resource<T> {
        Timber.d(message)
        return Resource.error("Network call has failed for a following reason: $message",null,responseCode)
    }


}
