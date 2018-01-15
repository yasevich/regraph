package com.github.yasevich.regraph.view

import android.support.v4.app.Fragment
import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.presenter.CurrencySelectionContract

class MainFragment : Fragment() {

    val presenter: CurrencySelectionContract.Presenter = App.instance.createMainScreenPresenter()

    init {
        retainInstance = true
    }
}
