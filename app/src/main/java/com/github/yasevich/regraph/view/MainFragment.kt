package com.github.yasevich.regraph.view

import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.CurrencySelectionContract

class MainFragment : PresenterHolderFragment<CurrencySelectionContract.Presenter>() {
    override val presenter: CurrencySelectionContract.Presenter = App.instance.createCurrencySelectionPresenter()
}
