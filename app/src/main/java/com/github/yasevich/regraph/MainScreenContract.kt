package com.github.yasevich.regraph

interface MainScreenContract {

    interface View {
        fun onInProgress(inProgress: Boolean)
        fun onCurrencies(currencies: List<String>)
        fun onRefused(error: CharSequence)
    }

    interface Presenter {
        var view: View?
        fun requestCurrencies()
    }
}
