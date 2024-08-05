package com.monsalud.fivedayforecaster

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.monsalud.fivedayforecaster.di.appModule

class FiveDayForecasterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FiveDayForecasterApplication)
            modules(
                listOf(
                    appModule
                )
            )
        }
    }
}