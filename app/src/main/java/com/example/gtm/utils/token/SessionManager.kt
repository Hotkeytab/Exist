package com.example.gtm.utils.token

import android.content.Context
import android.content.SharedPreferences
import com.example.gtm.R
import com.example.gtm.data.entities.ui.User

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


    /**
     * Function to fetch user
     */
    fun fetchUser(): User {


        val id = prefs.getInt("id", 0)
        val roleId = prefs.getInt("roleId", 0)
        val firstname = prefs.getString("firstname", "")
        val lastname = prefs.getString("lastname", "")
        val email = prefs.getString("email", "")
        val password = prefs.getString("password", "")
        val phone = prefs.getString("phone", "")
        val enabled = prefs.getString("enabled", "")
        val gender = prefs.getString("gender", "")
        return User(id,firstname!!,lastname!!,email!!,password!!,phone!!,true,gender!!,roleId)
    }

}
