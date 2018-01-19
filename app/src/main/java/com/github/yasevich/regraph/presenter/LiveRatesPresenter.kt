package com.github.yasevich.regraph.presenter

import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.UPDATES_RATE_MS
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import com.github.yasevich.regraph.repository.RepositoryResponse
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class LiveRatesPresenter(private val repository: CurrencyRateRepository): LiveRatesContract.Presenter {

    override var view: LiveRatesContract.View? = null

    private lateinit var baseCurrency: String
    private lateinit var currencies: List<String>
    private lateinit var history: Map<String, CurrencyRates>

    private var timer: Timer? = null

    override fun setCurrencies(currencies: List<String>) {
        this.currencies = currencies
        this.baseCurrency = currencies[0]
        this.history = currencies
                .asSequence()
                .map { CurrencyRates(it) }
                .associateBy { it.name }

        onNewBaseCurrency(baseCurrency)
        restartIfNeeded()
    }

    override fun setBaseCurrency(baseCurrency: String) {
        if (currencies.contains(baseCurrency)) {
            this.baseCurrency = baseCurrency
            onNewBaseCurrency(baseCurrency)
            restartIfNeeded()
        } else {
            onRefused(AppError.INVALID_CURRENCY)
        }
    }

    override fun startUpdates() {
        stopUpdates()
        async {
            timer = fixedRateTimer(period = UPDATES_RATE_MS) {
                val rates = repository.getRates(baseCurrency, currencies.toSet())
                mainThread {
                    onCurrenciesResponse(rates)
                }
            }
        }
    }

    override fun stopUpdates() {
        timer?.cancel()
    }

    private fun restartIfNeeded() {
        if (timer != null) {
            stopUpdates()
            startUpdates()
        }
    }

    private fun onCurrenciesResponse(response: RepositoryResponse<List<CurrencyRate>>) {
        when (response.status) {
            AppStatus.SUCCESS -> onNewRates(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
    }

    private fun onNewBaseCurrency(baseCurrency: String) {
        view?.onNewBaseCurrency(baseCurrency, currencies)
    }

    private fun onNewRates(rates: List<CurrencyRate>) {
        rates.forEach {
            history[it.currency.currencyCode]?.add(it)
        }
        view?.onNewRates(history.values.map { it.graph })
    }

    private fun onRefused(error: AppError) {
        val textResId = when(error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        view?.onError(textResId)
    }
}
