package com.github.yasevich.regraph.presenter

import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import com.github.yasevich.regraph.repository.RepositoryResponse
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class CurrencySelectionPresenter(private val repository: CurrencyRateRepository) : CurrencySelectionContract.Presenter {

    override var view: CurrencySelectionContract.View? = null

    private val currencies: MutableList<String> = mutableListOf()
    private val selectedCurrencies: MutableList<String> = mutableListOf()
    private val currenciesSelection: List<Pair<String, Boolean>>
        get() = currencies.map { Pair(it, selectedCurrencies.contains(it)) }

    private var inProgress: Boolean = false

    override fun requestCurrencies() {
        if (inProgress) {
            view?.onCurrenciesLoading(true)
            return
        }
        onInProgress(true)

        async {
            val response = repository.getCurrencies()
            mainThread {
                onCurrenciesResponse(response)
            }
        }
    }

    override fun addSelectedCurrency(currency: String) {
        selectedCurrencies.add(currency)
        onSelectionChanged(currency)
    }

    override fun removeSelectedCurrency(currency: String) {
        selectedCurrencies.remove(currency)
        onSelectionChanged(currency)
    }

    private fun onCurrenciesResponse(response: RepositoryResponse<List<String>>) {
        when (response.status) {
            AppStatus.SUCCESS -> onCurrencies(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
        onInProgress(false)
    }

    private fun onInProgress(inProgress: Boolean) {
        this.inProgress = inProgress
        view?.onCurrenciesLoading(inProgress)
    }

    private fun onCurrencies(currencies: List<String>) {
        with(this.currencies) {
            clear()
            addAll(currencies)
        }
        selectedCurrencies.retainAll(currencies)
        view?.onCurrencies(currenciesSelection)
        onSelectedCurrencies()
    }

    private fun onRefused(error: AppError) {
        val resId = when (error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        view?.onError(resId)
    }

    private fun onSelectionChanged(currency: String) {
        view?.onCurrencySelectionChanged(currenciesSelection, currencies.indexOf(currency))
        onSelectedCurrencies()
    }

    private fun onSelectedCurrencies() {
        view?.onCurrenciesSelection(selectedCurrencies.isNotEmpty())
    }
}
