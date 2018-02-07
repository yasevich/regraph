package com.github.yasevich.regraph.view

import android.os.Bundle
import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class LiveRatesFragment: PresenterHolderFragment<
        LiveRatesContract.View,
        LiveRatesContract.ViewState,
        LiveRatesContract.Presenter>() {

    override val presenter: LiveRatesContract.Presenter by lazy {
        AsyncWrappingPresenter(App.instance.createLiveRatesPresenter(
                arguments!!.getStringArrayList(LiveRatesActivity.EXTRA_CURRENCIES)))
    }

    override fun wrap(bundle: Bundle): LiveRatesContract.ViewState = State(bundle)

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

    private class State(private val bundle: Bundle) : LiveRatesContract.ViewState {

        private val keyBaseCurrency: String = "baseCurrency"

        override var baseCurrency: String
            get() = bundle.getString(keyBaseCurrency)
            set(value) {
                bundle.putString(keyBaseCurrency, value)
            }
    }
}
