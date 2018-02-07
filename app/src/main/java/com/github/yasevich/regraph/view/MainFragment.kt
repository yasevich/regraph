package com.github.yasevich.regraph.view

import android.os.Bundle
import com.github.yasevich.regraph.App
import com.github.yasevich.regraph.CurrencySelectionContract
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class MainFragment : PresenterHolderFragment<
        CurrencySelectionContract.View,
        CurrencySelectionContract.ViewState,
        CurrencySelectionContract.Presenter>() {

    override val presenter: CurrencySelectionContract.Presenter =
            AsyncWrappingPresenter(App.instance.createCurrencySelectionPresenter())

    override fun wrap(bundle: Bundle): CurrencySelectionContract.ViewState = State(bundle)

    private class AsyncWrappingPresenter(private val presenter: CurrencySelectionContract.Presenter) :
            CurrencySelectionContract.Presenter by presenter {

        override var view: CurrencySelectionContract.View?
            get() = presenter.view
            set(value) {
                presenter.view = if (value != null) MainThreadWrappingView(value) else null
            }

        override fun requestCurrencies() {
            async {
                presenter.requestCurrencies()
            }
        }

        private class MainThreadWrappingView(private val view: CurrencySelectionContract.View) :
                CurrencySelectionContract.View by view {

            override fun onCurrencies(currencies: List<Pair<String, Boolean>>) {
                mainThread {
                    view.onCurrencies(currencies)
                }
            }

            override fun onCurrenciesLoading(inProgress: Boolean) {
                mainThread {
                    view.onCurrenciesLoading(inProgress)
                }
            }

            override fun onCurrenciesSelection(valid: Boolean) {
                mainThread {
                    view.onCurrenciesSelection(valid)
                }
            }

            override fun onError(textResId: Int) {
                mainThread {
                    view.onError(textResId)
                }
            }
        }
    }

    private class State(private val bundle: Bundle) : CurrencySelectionContract.ViewState {

        private val keySelectedCurrencies: String = "selectedCurrencies"

        override var selectedCurrencies: List<String>
            get() = bundle.getStringArrayList(keySelectedCurrencies)
            set(value) {
                bundle.putStringArrayList(keySelectedCurrencies, ArrayList(value))
            }
    }
}
