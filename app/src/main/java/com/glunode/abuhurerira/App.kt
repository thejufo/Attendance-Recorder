// Created by abdif on 8/2/2020

package com.glunode.abuhurerira

import android.app.Application
import com.glunode.api.data.AppRepository
import timber.log.Timber

class App : Application() {

    lateinit var appRepo: AppRepository

    override fun onCreate() {
        super.onCreate()

        appRepo = AppRepository.getDefault()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}