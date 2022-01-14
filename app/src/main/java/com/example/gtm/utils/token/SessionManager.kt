package com.example.gtm.utils.token

import android.content.Context
import android.content.SharedPreferences
import com.example.gtm.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val Authorization_Refresh = "token"
    }

    /**
     * Function to save auth token
     */

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(Authorization_Refresh, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchRefreshToken(): String? {
        return prefs.getString(Authorization_Refresh, null)
    }

}
