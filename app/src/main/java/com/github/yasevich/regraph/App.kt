package com.github.yasevich.regraph

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.github.yasevich.regraph.presenter.CurrencySelectionContract
import com.github.yasevich.regraph.presenter.CurrencySelectionPresenter
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository

class App : Application() {

    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun createMainScreenPresenter(): CurrencySelectionContract.Presenter =
            CurrencySelectionPresenter(AssetCurrencyRateRepository(this))

    companion object {
        lateinit var instance: App
            private set
    }
}
