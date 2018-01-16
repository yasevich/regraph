package com.github.yasevich.regraph.view

import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.LiveRatesContract

class LiveRatesFragment: PresenterHolderFragment<LiveRatesContract.Presenter>() {
    override val presenter: LiveRatesContract.Presenter = App.instance.createLiveRatesPresenter()
}
