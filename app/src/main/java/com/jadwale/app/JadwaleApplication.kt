package com.jadwale.app

import android.app.Application
import com.jadwale.core.session.SessionStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JadwaleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionStore.init(this)
    }
}
