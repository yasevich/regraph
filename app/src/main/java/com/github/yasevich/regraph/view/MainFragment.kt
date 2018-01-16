package com.github.yasevich.regraph.view

import android.support.v4.app.Fragment
import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.presenter.CurrencySelectionContract

class MainFragment : Fragment() {

    val presenter: CurrencySelectionContract.Presenter = App.instance.createCurrencySelectionPresenter()

    init {
        retainInstance = true
    }
}
