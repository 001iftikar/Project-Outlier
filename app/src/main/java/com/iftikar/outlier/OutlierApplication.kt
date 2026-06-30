package com.iftikar.outlier

import android.app.Application
import com.descope.Descope
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OutlierApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Descope.setup(this, projectId = "P3Fliub39ehQeGVh5ueFQAhvTp5X")
    }
}