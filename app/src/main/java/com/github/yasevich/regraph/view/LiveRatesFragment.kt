package com.github.yasevich.regraph.view

import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class LiveRatesFragment: PresenterHolderFragment<LiveRatesContract.Presenter>() {

    override val presenter: LiveRatesContract.Presenter =
            AsyncWrappingPresenter(App.instance.createLiveRatesPresenter())

    private class AsyncWrappingPresenter(private val presenter: LiveRatesContract.Presenter) :
            LiveRatesContract.Presenter by presenter {

        override var view: LiveRatesContract.View?
            get() = presenter.view
            set(value) {
                presenter.view = if (value != null) MainThreadWrappingView(value) else null
            }

        override fun startUpdates() {
            async {
                presenter.startUpdates()
            }
        }

        private class MainThreadWrappingView(private val view: LiveRatesContract.View) :
                LiveRatesContract.View by view {

            override fun onNewRates(history: CurrencyRatesHistory) {
                mainThread {
                    view.onNewRates(history)
                }
            }

            override fun onError(textResId: Int) {
                mainThread {
                    view.onError(textResId)
                }
            }
        }
    }
}
