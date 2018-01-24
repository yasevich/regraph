package com.github.yasevich.regraph.presenter

import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.RATES_HISTORY_SIZE
import com.github.yasevich.regraph.UPDATES_RATE_MS
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import com.github.yasevich.regraph.repository.RepositoryResponse
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class LiveRatesPresenter(private val repository: CurrencyRateRepository): LiveRatesContract.Presenter {

    override var view: LiveRatesContract.View? = null
        set(value) {
            field = value
            if (value != null) {
                onView()
            }
        }

    private val baseCurrencyIndex: Int
        get() = currencies.indexOf(baseCurrency)

    private var history: CurrencyRatesHistory? = null

    private lateinit var baseCurrency: String
    private lateinit var currencies: List<String>

    private var timer: Timer? = null

    override fun setCurrencies(currencies: List<String>) {
        this.currencies = currencies
        this.baseCurrency = currencies[0]

        onCurrencies()
    }

    override fun setBaseCurrency(baseCurrency: String) {
        if (currencies.contains(baseCurrency)) {
            this.baseCurrency = baseCurrency
            this.history?.rebase(baseCurrency)
            onBaseCurrency()
            onNewRates()
        } else {
            onRefused(AppError.INVALID_CURRENCY)
        }
    }

    override fun startUpdates() {
        stopUpdates()
        val response = repository.getHistory(baseCurrency, currencies.toSet())
        history = when (response.status) {
            AppStatus.SUCCESS -> response.result.also { onNewRates() }
            AppStatus.REFUSED -> CurrencyRatesHistory(RATES_HISTORY_SIZE)
        }

        timer = fixedRateTimer(initialDelay = UPDATES_RATE_MS, period = UPDATES_RATE_MS) {
            handle(repository.getRates(baseCurrency, currencies.toSet()))
        }
    }

    override fun stopUpdates() {
        timer?.cancel()
    }

    private fun onView() {
        onCurrencies()
    }

    private fun handle(response: RepositoryResponse<CurrencyRates>) {
        when (response.status) {
            AppStatus.SUCCESS -> addRates(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
    }

    private fun addRates(rates: CurrencyRates) {
        history?.add(rates)
        onNewRates()
    }

    private fun onBaseCurrency() {
        view?.onBaseCurrency(baseCurrencyIndex)
    }

    private fun onCurrencies() {
        view?.onCurrencies(currencies)
    }

    private fun onNewRates() {
        view?.onNewRates(history?.graphs ?: emptyList())
    }

    private fun onRefused(error: AppError) {
        val textResId = when (error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        view?.onError(textResId)
    }
}
