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

class LiveRatesPresenter(
        private val repository: CurrencyRateRepository,
        private val currencies: List<String>
): LiveRatesContract.Presenter {

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
    private var baseCurrency: String = currencies[0]

    private var timer: Timer? = null

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
            AppStatus.SUCCESS -> response.result
            AppStatus.REFUSED -> CurrencyRatesHistory(RATES_HISTORY_SIZE)
        }
        onNewRates()

        timer = fixedRateTimer(initialDelay = UPDATES_RATE_MS, period = UPDATES_RATE_MS) {
            handle(repository.getRates(baseCurrency, currencies.toSet()))
        }
    }

    override fun stopUpdates() {
        timer?.cancel()
    }

    override fun saveState(state: LiveRatesContract.ViewState) {
        state.baseCurrency = baseCurrency
    }

    override fun restoreState(state: LiveRatesContract.ViewState) {
        setBaseCurrency(state.baseCurrency)
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
        view?.onCurrencies(currencies, baseCurrencyIndex)
    }

    private fun onNewRates() {
        history?.also { view?.onNewRates(it) }
    }

    private fun onRefused(error: AppError) {
        val textResId = when (error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        view?.onError(textResId)
    }
}
