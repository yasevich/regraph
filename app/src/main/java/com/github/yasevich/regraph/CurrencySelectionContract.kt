package com.github.yasevich.regraph

import com.github.yasevich.regraph.base.BasePresenter
import com.github.yasevich.regraph.base.BaseView
import com.github.yasevich.regraph.base.BaseViewState

interface CurrencySelectionContract {

    interface View : BaseView {
        fun onCurrencies(currencies: List<Pair<String, Boolean>>)
        fun onCurrenciesLoading(inProgress: Boolean)
        fun onCurrenciesSelection(valid: Boolean)
        fun onCurrencySelectionChanged(currencies: List<Pair<String, Boolean>>, position: Int)
        fun onShowGraph(currencies: List<String>)
        fun onError(textResId: Int)
    }

    interface ViewState : BaseViewState {
        var selectedCurrencies: List<String>
    }

    interface Presenter : BasePresenter<View, ViewState> {
        fun requestCurrencies()
        fun addSelectedCurrency(currency: String)
        fun removeSelectedCurrency(currency: String)
        fun showGraph()
    }
}
