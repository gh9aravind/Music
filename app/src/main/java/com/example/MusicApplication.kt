package com.example

import android.app.Application
import com.example.crash.CrashHandler

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.install(this)
    }
}
