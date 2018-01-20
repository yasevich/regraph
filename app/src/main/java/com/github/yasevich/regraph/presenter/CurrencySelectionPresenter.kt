package com.github.yasevich.regraph.presenter

import com.github.yasevich.regraph.CurrencySelectionContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import com.github.yasevich.regraph.repository.RepositoryResponse

class CurrencySelectionPresenter(private val repository: CurrencyRateRepository) : CurrencySelectionContract.Presenter {

    override var view: CurrencySelectionContract.View? = null
        set(value) {
            field = value
            if (value != null) {
                onView()
            }
        }

    private val currencies: MutableList<String> = mutableListOf()
    private val selectedCurrencies: MutableList<String> = mutableListOf()
    private val currenciesSelection: List<Pair<String, Boolean>>
        get() = currencies.map { Pair(it, selectedCurrencies.contains(it)) }

    private var inProgress: Boolean = false
        set(value) {
            field = value
            onInProgress(value)
        }

    override fun requestCurrencies() {
        inProgress = true
        handle(repository.getCurrencies())
        inProgress = false
    }

    override fun addSelectedCurrency(currency: String) {
        selectedCurrencies.add(currency)
        onSelectionChanged(currency)
    }

    override fun removeSelectedCurrency(currency: String) {
        selectedCurrencies.remove(currency)
        onSelectionChanged(currency)
    }

    override fun showGraph() {
        view?.onShowGraph(selectedCurrencies)
    }

    private fun handle(response: RepositoryResponse<List<String>>) {
        when (response.status) {
            AppStatus.SUCCESS -> setCurrencies(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
    }

    private fun setCurrencies(currencies: List<String>) {
        with(this.currencies) {
            clear()
            addAll(currencies)
        }
        selectedCurrencies.retainAll(currencies)
        onCurrencies()
    }

    private fun onView() {
        onInProgress(inProgress)
        onCurrencies()
    }

    private fun onInProgress(inProgress: Boolean) {
        view?.onCurrenciesLoading(inProgress)
    }

    private fun onCurrencies() {
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
        view?.onCurrenciesSelection(selectedCurrencies.size >= 2)
    }
}
