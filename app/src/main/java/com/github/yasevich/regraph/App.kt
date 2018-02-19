package com.github.yasevich.regraph

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.github.yasevich.regraph.presenter.CurrencySelectionPresenter
import com.github.yasevich.regraph.presenter.LiveRatesPresenter
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository
import com.github.yasevich.regraph.repository.CurrencyRateRepository

class App : Application() {

    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val repository: CurrencyRateRepository by lazy { AssetCurrencyRateRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun createCurrencySelectionPresenter(): CurrencySelectionContract.Presenter = CurrencySelectionPresenter(repository)

    fun createLiveRatesPresenter(currencies: List<String>): LiveRatesContract.Presenter =
            LiveRatesPresenter(repository, currencies)

    companion object {
        lateinit var instance: App
            private set
    }
}
