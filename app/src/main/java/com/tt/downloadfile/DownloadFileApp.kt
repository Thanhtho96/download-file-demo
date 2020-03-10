package com.tt.downloadfile

import android.app.Application
import com.tt.downloadfile.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DownloadFileApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DownloadFileApp)
            androidLogger()
            modules(appComponent)
        }
    }
}