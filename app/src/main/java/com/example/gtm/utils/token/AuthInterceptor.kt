package com.example.gtm.utils.token

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.gtm.R
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()



        // If token has been saved, add it to the request
        sessionManager.fetchRefreshToken()?.let {
            requestBuilder.addHeader("Authorization", "bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
