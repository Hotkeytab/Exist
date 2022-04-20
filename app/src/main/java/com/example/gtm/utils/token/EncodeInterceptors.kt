package com.example.gtm.utils.token

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class EncodeInterceptors : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.toString()

        val string = path.replace("%22", "\"") // replace


        val newRequest = request.newBuilder()
            .url(string)
            .build()

        return chain.proceed(newRequest)
    }


}