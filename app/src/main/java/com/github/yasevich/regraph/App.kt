package com.github.yasevich.regraph

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository
import com.github.yasevich.regraph.repository.CurrencyRateRepository

class App : Application() {

    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val repository: CurrencyRateRepository by lazy { AssetCurrencyRateRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
