package com.example.gtm.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.gtm.R
import com.example.gtm.ui.auth.AuthActivity
import com.example.gtm.utils.animations.UiAnimations
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    private val uiAnimations = UiAnimations(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        //Hide Nav Bar
        uiAnimations.hideNavBar()

        //To Login Activity
        toLogin()
    }


    //Splashscreen duration Then to Login
    private fun toLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }



}