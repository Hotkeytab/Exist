package com.example.gtm.utils.token

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)
    lateinit var sharedPref: SharedPreferences
    private var context = context

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        Log.i("Called","Called")


        val sharedPref = context.getSharedPreferences(
            "WiWi",
            Context.MODE_PRIVATE
        )

        val  name = sharedPref.getString("token", "")
        Log.i("Called","$name")
        requestBuilder.addHeader("token", "$name")
        // If token has been saved, add it to the request
        sessionManager.fetchRefreshToken()?.let {
            requestBuilder.addHeader("token", it)
        }



        return chain.proceed(requestBuilder.build())
    }
}
