package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.repository.RepositoryResponse
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class MainScreenPresenter : MainScreenContract.Presenter {

    override var view: MainScreenContract.View? = null

    private var inProgress: Boolean = false

    override fun requestCurrencies() {
        if (inProgress) {
            view?.onInProgress(true)
            return
        }
        onInProgress(true)

        async {
            val response = App.instance.repository.getCurrencies()
            mainThread {
                onCurrenciesResponse(response)
            }
        }
    }

    private fun onCurrenciesResponse(response: RepositoryResponse<List<String>>) {
        when (response.status) {
            AppStatus.SUCCESS -> onCurrencies(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
        onInProgress(false)
    }

    private fun onInProgress(inProgress: Boolean) {
        this.inProgress = inProgress
        view?.onInProgress(inProgress)
    }

    private fun onCurrencies(currencies: List<String>) {
        view?.onCurrencies(currencies)
    }

    private fun onRefused(error: AppError) {
        val resId = when (error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        view?.onRefused(resId)
    }
}
