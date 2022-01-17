package com.example.gtm.utils.animations

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController

class UiAnimations(activity: Activity) {


    private var activity = activity

    // hide the navigation bar.
    fun hideNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.setDecorFitsSystemWindows(false)
            if (activity.window.insetsController != null) {
                activity.window.insetsController!!.hide(WindowInsets.Type.navigationBars())
                activity.window.insetsController!!.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

    }
}