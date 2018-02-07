package com.github.yasevich.regraph

import com.github.yasevich.regraph.base.BasePresenter
import com.github.yasevich.regraph.base.BaseView
import com.github.yasevich.regraph.base.BaseViewState
import com.github.yasevich.regraph.model.CurrencyRatesHistory

interface LiveRatesContract {

    interface View : BaseView {
        fun onBaseCurrency(baseIndex: Int)
        fun onCurrencies(currencies: List<String>, baseIndex: Int)
        fun onNewRates(history: CurrencyRatesHistory)
        fun onError(textResId: Int)
    }

    interface ViewState : BaseViewState {
        var baseCurrency: String
    }

    interface Presenter : BasePresenter<View, ViewState> {
        fun setBaseCurrency(baseCurrency: String)
        fun startUpdates()
        fun stopUpdates()
    }
}
