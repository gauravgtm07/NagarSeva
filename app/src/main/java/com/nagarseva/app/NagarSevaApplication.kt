package com.nagarseva.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NagarSevaApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Any app-level initialization here
    }
}
