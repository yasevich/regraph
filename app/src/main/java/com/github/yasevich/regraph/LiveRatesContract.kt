package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.Graph

interface LiveRatesContract {

    interface View {
        fun onNewBaseCurrency(baseCurrency: String, currencies: List<String>)
        fun onNewRates(graphs: List<Graph>)
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
