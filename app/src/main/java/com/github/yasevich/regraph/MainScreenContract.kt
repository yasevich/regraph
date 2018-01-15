package com.github.yasevich.regraph

interface MainScreenContract {

    interface View {
        fun onInProgress(inProgress: Boolean)
        fun onCurrencies(currencies: List<String>)
        fun onRefused(textResId: Int)
    }

    interface Presenter {
        var view: View?
        fun requestCurrencies()
    }
}
