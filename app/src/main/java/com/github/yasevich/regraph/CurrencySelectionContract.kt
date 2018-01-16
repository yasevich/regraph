package com.github.yasevich.regraph

interface CurrencySelectionContract {

    interface View {
        fun onCurrencies(currencies: List<Pair<String, Boolean>>)
        fun onCurrenciesLoading(inProgress: Boolean)
        fun onCurrenciesSelection(valid: Boolean)
        fun onCurrencySelectionChanged(currencies: List<Pair<String, Boolean>>, position: Int)
        fun onShowGraph(currencies: List<String>)
        fun onError(textResId: Int)
    }

    interface Presenter {
        var view: View?
        fun requestCurrencies()
        fun addSelectedCurrency(currency: String)
        fun removeSelectedCurrency(currency: String)
        fun showGraph()
    }
}