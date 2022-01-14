package com.example.gtm.ui

import android.app.Application
import com.example.gtm.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }
    }
}