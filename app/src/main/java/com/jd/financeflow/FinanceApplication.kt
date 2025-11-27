package com.jd.financeflow

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

import com.jd.financeflow.di.appModule

/**
 * Custom application class for the FinanceFlow application.
 * This class is responsible for initializing the Koin dependency injection framework.
 */
class FinanceFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FinanceFlowApplication)
            modules(appModule)
        }
    }
}
