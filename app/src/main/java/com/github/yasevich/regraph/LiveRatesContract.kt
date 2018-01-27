package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.CurrencyRatesHistory

interface LiveRatesContract {

    interface View {
        fun onBaseCurrency(baseIndex: Int)
        fun onCurrencies(currencies: List<String>)
        fun onNewRates(history: CurrencyRatesHistory)
        fun onError(textResId: Int)
    }

    interface Presenter {
        var view: View?
        fun setCurrencies(currencies: List<String>)
        fun setBaseCurrency(baseCurrency: String)
        fun startUpdates()
        fun stopUpdates()
    }
}
